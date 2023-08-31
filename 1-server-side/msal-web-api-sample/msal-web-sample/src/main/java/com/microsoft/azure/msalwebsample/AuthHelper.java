// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.msalwebsample;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.microsoft.aad.msal4j.*;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class AuthHelper {

    static final String PRINCIPAL_SESSION_NAME = "principal";
    static final String TOKEN_CACHE_SESSION_ATTRIBUTE = "token_cache";
    static final String END_SESSION_ENDPOINT = "https://login.microsoftonline.com/common/oauth2/v2.0/logout";

    private String clientId;
    private String clientSecret;
    private String authority;
    private String redirectUri;
    private String oboDefaultScope;
    private String logoutRedirectUrl;

    @Autowired
    BasicConfiguration configuration;

    @PostConstruct
    public void init() {
        clientId = configuration.getClientId();
        authority = configuration.getAuthority();
        clientSecret = configuration.getSecretKey();
        redirectUri = configuration.getRedirectUri();
        oboDefaultScope = configuration.getOboDefaultScope();
        logoutRedirectUrl = configuration.getLogoutRedirectUri();
    }

    private ConfidentialClientApplication createClientApplication() {
        ConfidentialClientApplication cca;
        try {
            cca = ConfidentialClientApplication.builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret)).
                    authority(authority).
                    build();
        } catch (Exception ex) {
            throw new AuthException(String.format("Error creating client application object: %s", ex.getMessage()),
                    ex.getCause());
        }
        return cca;
    }

    IAuthenticationResult getAuthResultBySilentFlow(HttpServletRequest httpRequest, String scope) throws Exception {
        IAuthenticationResult result = AuthHelper.getAuthSessionObject(httpRequest);

        ConfidentialClientApplication app;
        app = createClientApplication();

        Object tokenCache = httpRequest.getSession().getAttribute("token_cache");
        if (tokenCache != null) {
            app.tokenCache().deserialize(tokenCache.toString());
        }

        SilentParameters parameters = SilentParameters.builder(
                Collections.singleton(scope),
                result.account()).build();

        IAuthenticationResult updatedResult = app.acquireTokenSilently(parameters).get();

        //update session with latest token cache
        storeTokenCacheInSession(httpRequest, app.tokenCache().serialize());
        return updatedResult;
    }

    IAuthenticationResult getAuthResultByAuthCode(
            HttpServletRequest httpServletRequest,
            AuthorizationCode authorizationCode,
            String currentUri) {

        IConfidentialClientApplication app;
        IAuthenticationResult result;
        try{
            app = createClientApplication();

            String authCode = authorizationCode.getValue();
            AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
                    authCode, new URI(currentUri))
                    .build();

            result = app.acquireToken(parameters).get();

        } catch(Exception ex){
            throw new AuthException(String.format("Error running authentication code flow: %s", ex.getMessage()),
                    ex.getCause());
        }
        if (result == null) {
            throw new AuthException("Authentication result is null");
        }

        storeTokenCacheInSession(httpServletRequest, app.tokenCache().serialize());
        return result;
    }

    String getRedirectUrl(String state, String nonce) {
        ConfidentialClientApplication cca = createClientApplication();

        AuthorizationRequestUrlParameters parameters =
                AuthorizationRequestUrlParameters
                        .builder(redirectUri,
                                new HashSet<>(Arrays.asList(oboDefaultScope)))
                        .responseMode(ResponseMode.FORM_POST)
                        .prompt(Prompt.SELECT_ACCOUNT)
                        .state(state)
                        .nonce(nonce)
                        .build();

        return cca.getAuthorizationRequestUrl(parameters).toString();
    }

    private void storeTokenCacheInSession(HttpServletRequest httpServletRequest, String tokenCache) {
        httpServletRequest.getSession().setAttribute(AuthHelper.TOKEN_CACHE_SESSION_ATTRIBUTE, tokenCache);
    }

    void setSessionPrincipal(HttpServletRequest httpRequest, IAuthenticationResult result) {
        httpRequest.getSession().setAttribute(AuthHelper.PRINCIPAL_SESSION_NAME, result);
    }

    void removePrincipalFromSession(HttpServletRequest httpRequest) {
        httpRequest.getSession().removeAttribute(AuthHelper.PRINCIPAL_SESSION_NAME);
    }

    static boolean isAuthenticationSuccessful(AuthenticationResponse authResponse) {
        return authResponse instanceof AuthenticationSuccessResponse;
    }

    static boolean isAuthenticated(HttpServletRequest request) {
        return request.getSession().getAttribute(PRINCIPAL_SESSION_NAME) != null;
    }

    static IAuthenticationResult getAuthSessionObject(HttpServletRequest request) {
        Object principalSession = request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
        if (principalSession instanceof IAuthenticationResult) {
            return (IAuthenticationResult) principalSession;
        } else {
            throw new IllegalStateException();
        }
    }

    static boolean containsAuthenticationCode(HttpServletRequest httpRequest) {

        Map<String, String[]> httpParameters = httpRequest.getParameterMap();

        boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
        boolean containsErrorData = httpParameters.containsKey("error");
        boolean containIdToken = httpParameters.containsKey("id_token");
        boolean containsCode = httpParameters.containsKey("code");

        return isPostRequest && (containsErrorData || containsCode || containIdToken);
    }

    public String getOboDefaultScope() {
        return oboDefaultScope;
    }

    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }
}
