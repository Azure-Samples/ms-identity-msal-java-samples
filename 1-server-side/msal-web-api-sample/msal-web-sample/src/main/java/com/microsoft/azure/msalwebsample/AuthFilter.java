// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.msalwebsample;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.microsoft.aad.msal4j.*;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private static final String STATES = "states";
    private static final String STATE = "state";
    private static final Integer STATE_TTL = 3600;
    private static final String FAILED_TO_VALIDATE_MESSAGE = "Failed to validate data received from Authorization service - ";

    private List<String> excludedUrls = Arrays.asList("/", "/favicon.ico");

    @Autowired
    AuthHelper authHelper;

    @Override
    public boolean shouldNotFilter(HttpServletRequest httpRequest) {
        String path = httpRequest.getServletPath();
        return excludedUrls.contains(path);
    }

    @Override
    public void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                                 FilterChain chain) throws IOException, ServletException {
        try {
            String currentUri = httpRequest.getRequestURL().toString();
            String queryStr = httpRequest.getQueryString();
            String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");

            // check if user has a AuthData in the session
            if (!AuthHelper.isAuthenticated(httpRequest)) {
                if (AuthHelper.containsAuthenticationCode(httpRequest)) {
                    // response should have authentication code, which will be used to acquire access token
                    processAuthenticationCodeRedirect(httpRequest, currentUri, fullUrl);
                } else {
                    // not authenticated, redirecting to login.microsoft.com so user can authenticate
                    sendAuthRedirect(httpRequest, httpResponse);
                    return;
                }
            }
            if (isAccessTokenExpired(httpRequest)) {
                // Attempt to refresh tokens and session
                IAuthenticationResult result = authHelper.getAuthResultBySilentFlow(
                        httpRequest,
                        authHelper.getOboDefaultScope());
                authHelper.setSessionPrincipal(httpRequest, result);
            }
        } catch (MsalException msalException) {
            // something went wrong (like expiration or revocation of token)
            // we should invalidate AuthData stored in session and redirect to Authorization server
            authHelper.removePrincipalFromSession(httpRequest);
            sendAuthRedirect(httpRequest, httpResponse);
            return;
        } catch (Exception ex) {
            httpResponse.setStatus(500);
            httpRequest.setAttribute("error", ex.getMessage());
            httpRequest.getRequestDispatcher("/error").forward(httpRequest, httpResponse);
            return;
        }
        chain.doFilter(httpRequest, httpResponse);
    }

    private boolean isAccessTokenExpired(HttpServletRequest httpRequest) {
        IAuthenticationResult result = AuthHelper.getAuthSessionObject(httpRequest);
        return result.expiresOnDate().before(new Date());
    }

    private void processAuthenticationCodeRedirect(HttpServletRequest httpRequest, String currentUri, String fullUrl) {

        Map<String, List<String>> params = new HashMap<>();
        for (String key : httpRequest.getParameterMap().keySet()) {
            params.put(key, Collections.singletonList(httpRequest.getParameterMap().get(key)[0]));
        }

        if (params.get("error") != null) {
            throw new AuthException(String.format("AAD returned an error response: %s - %s",
                    params.get("error"),
                    params.get("error_description")));
        }

        // validate that state in response equals to state in request
        StateData stateData = validateState(httpRequest.getSession(), params.get(STATE).get(0));

        AuthenticationResponse authResponse = parseRedirect(fullUrl, params);
        if (AuthHelper.isAuthenticationSuccessful(authResponse)) {
            AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
            // validate that OIDC Auth Response matches Code Flow (contains only requested artifacts)
            validateAuthRespMatchesAuthCodeFlow(oidcResponse);

            IAuthenticationResult result = authHelper.getAuthResultByAuthCode(
                    httpRequest,
                    oidcResponse.getAuthorizationCode(),
                    currentUri);

            // validate nonce to prevent reply attacks (code maybe substituted to one with broader access)
            validateNonce(stateData, getNonceClaimValueFromIdToken(result.idToken()));

            authHelper.setSessionPrincipal(httpRequest, result);
        } else {
            AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
            throw new AuthException(String.format("Request for auth code failed: %s - %s",
                    oidcResponse.getErrorObject().getCode(),
                    oidcResponse.getErrorObject().getDescription()));
        }
    }

    private AuthenticationResponse parseRedirect(String fullUrl, Map<String, List<String>> params) {

        AuthenticationResponse authResponse;
        try {
            authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);
        } catch (Exception ex) {
            throw new AuthException(String.format("Error parsing auth code redirect: %s", ex.getMessage()),
                    ex.getCause());
        }

        return authResponse;
    }

    private void sendAuthRedirect(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        // state parameter to validate response from Authorization server and nonce parameter to validate idToken
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        storeStateInSession(httpRequest.getSession(), state, nonce);

        httpResponse.setStatus(302);
        String redirectUrl = authHelper.getRedirectUrl(state, nonce);
        httpResponse.sendRedirect(redirectUrl);
    }

    private void validateNonce(StateData stateData, String nonce) {
        if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
            throw new AuthException(FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
        }
    }

    private String getNonceClaimValueFromIdToken(String idToken) {
        String nonce;
        try {
            nonce = (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim("nonce");
        } catch (Exception ex) {
            throw new AuthException(String.format("Could not to parse id token: %s", ex.getMessage()),
                    ex.getCause());
        }
        return nonce;
    }

    private StateData validateState(HttpSession session, String state) {
        if (StringUtils.isNotEmpty(state)) {
            StateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new AuthException(FAILED_TO_VALIDATE_MESSAGE + "could not validate state");
    }

    private void validateAuthRespMatchesAuthCodeFlow(AuthenticationSuccessResponse oidcResponse) {
        if (oidcResponse.getIDToken() != null || oidcResponse.getAccessToken() != null ||
                oidcResponse.getAuthorizationCode() == null) {
            throw new AuthException(FAILED_TO_VALIDATE_MESSAGE + "unexpected set of artifacts received");
        }
    }

    private void storeStateInSession(HttpSession session, String state, String nonce) {
        if (session.getAttribute(STATES) == null) {
            session.setAttribute(STATES, new HashMap<String, StateData>());
        }
        ((Map<String, StateData>) session.getAttribute(STATES)).put(state, new StateData(nonce, new Date()));
    }

    private StateData removeStateFromSession(HttpSession session, String state) {
        Map<String, StateData> states = (Map<String, StateData>) session.getAttribute(STATES);
        if (states != null) {
            eliminateExpiredStates(states);
            StateData stateData = states.get(state);
            if (stateData != null) {
                states.remove(state);
                return stateData;
            }
        }
        return null;
    }

    private void eliminateExpiredStates(Map<String, StateData> map) {
        Iterator<Map.Entry<String, StateData>> it = map.entrySet().iterator();

        Date currTime = new Date();
        while (it.hasNext()) {
            Map.Entry<String, StateData> entry = it.next();
            long diffInSeconds = TimeUnit.MILLISECONDS.
                    toSeconds(currTime.getTime() - entry.getValue().getExpirationDate().getTime());

            if (diffInSeconds > STATE_TTL) {
                it.remove();
            }
        }
    }
}
