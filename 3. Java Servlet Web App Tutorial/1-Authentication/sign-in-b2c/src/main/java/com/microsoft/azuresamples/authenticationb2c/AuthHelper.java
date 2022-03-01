package com.microsoft.azuresamples.authenticationb2c;

import com.microsoft.aad.msal4j.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.logging.Level;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthHelper {
    static final String AUTHORITY = Config.getProperty("aad.authority");
    static final String SIGN_IN_POLICY = Config.getProperty("aad.signInPolicy");
    static final String PW_RESET_POLICY = Config.getProperty("aad.passwordResetPolicy");
    static final String EDIT_PROFILE_POLICY = Config.getProperty("aad.editProfilePolicy");
    static final String CLIENT_ID = Config.getProperty("aad.clientId");
    static final String SECRET = Config.getProperty("aad.secret");
    static final String SCOPES = Config.getProperty("aad.scopes");
    static final String SIGN_OUT_ENDPOINT = Config.getProperty("aad.signOutEndpoint");
    static final String POST_SIGN_OUT_FRAGMENT = Config.getProperty("aad.postSignOutFragment");
    static final Long STATE_TTL = Long.parseLong(Config.getProperty("app.stateTTL"));
    static final String REDIRECT_URI = Config.getProperty("app.redirectUri");
    static final String HOME_PAGE = Config.getProperty("app.homePage");

    public static ConfidentialClientApplication getConfidentialClientInstance(final String policy)
            throws Exception {
        ConfidentialClientApplication confClientInstance = null;
        Config.logger.log(Level.INFO, "Getting confidential client instance");
        try {
            final IClientSecret secret = ClientCredentialFactory.createFromSecret(SECRET);
            confClientInstance = ConfidentialClientApplication.builder(CLIENT_ID, secret)
                    .b2cAuthority(AUTHORITY + policy).build();
        } catch (final Exception ex) {
            Config.logger.log(Level.SEVERE, "Failed to create Confidential Client Application.");
            throw ex;
        }
        return confClientInstance;
    }

    public static MsalAuthSession getMsalAuthSession(final HttpSession session) {
        return MsalAuthSession.getMsalAuthSession(session);
    }

    public static void signIn(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
        Config.logger.log(Level.INFO, "sign-in sign-up flow init");
        AuthHelper.authorize(req, resp, SIGN_IN_POLICY); // authorize tries to do non-interactive auth first
    }

    public static void editProfile(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
        Config.logger.log(Level.INFO, "edit profile flow init");
        AuthHelper.redirectToAuthorizationEndpoint(req, resp, EDIT_PROFILE_POLICY);
    }

    public static void passwordReset(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
        Config.logger.log(Level.INFO, "password reset flow init");
        AuthHelper.redirectToAuthorizationEndpoint(req, resp, PW_RESET_POLICY);
    }

    public static void signOut(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
        Config.logger.log(Level.INFO, "sign out init");
        AuthHelper.redirectToSignoutEndpoint(req, resp);
    }

    public static void redirectToSignoutEndpoint(final HttpServletRequest req, final HttpServletResponse resp)
            throws Exception {
        req.getSession().invalidate();
        final String redirect = String.format("%s%s%s%s%s", AUTHORITY, SIGN_IN_POLICY, SIGN_OUT_ENDPOINT,
                POST_SIGN_OUT_FRAGMENT, URLEncoder.encode(HOME_PAGE, "UTF-8"));
        Config.logger.log(Level.INFO, "Redirecting user to {0}", redirect);
        resp.setStatus(302);
        resp.sendRedirect(redirect);
    }

    private static void authorize(final HttpServletRequest req, final HttpServletResponse resp, final String policy)
            throws Exception {

        final MsalAuthSession msalAuth = getMsalAuthSession(req.getSession());
        Config.logger.log(Level.INFO, "preparing to authorize");
        final IAuthenticationResult authResult = msalAuth.getAuthResult();
        if (authResult != null) {
            Config.logger.log(Level.INFO, "found auth result in session. trying to silently acquire token...");
            acquireTokenSilently(req, resp, policy, authResult.account());
        } else {
            Config.logger.log(Level.INFO, "did not find auth result in session. trying to interactively acquire token...");
            redirectToAuthorizationEndpoint(req, resp, policy);
        }
    }

    private static void acquireTokenSilently(final HttpServletRequest req, final HttpServletResponse resp,
            final String policy, final IAccount account) throws Exception {
        final MsalAuthSession msalAuth = getMsalAuthSession(req.getSession());
        final SilentParameters parameters = SilentParameters.builder(Collections.singleton(SCOPES), account).build();

        try {
            final ConfidentialClientApplication client = getConfidentialClientInstance(policy);
            client.tokenCache().deserialize(msalAuth.getTokenCache());
            Config.logger.log(Level.INFO, "preparing to acquire silently");
            final CompletableFuture<IAuthenticationResult> future = client.acquireTokenSilently(parameters);
            final IAuthenticationResult result = future.get();
            Config.logger.log(Level.INFO, "got future!");
            if (result != null) {
                Config.logger.log(Level.INFO, "silent auth returned result. attempting to parse and process...");
                parseJWTClaimsSetAndStoreResultInSession(msalAuth, result, client.tokenCache().serialize());
                processSuccessfulAuthentication(msalAuth);
                resp.setStatus(302);
                resp.sendRedirect(HOME_PAGE);
            } else {
                Config.logger.log(Level.INFO, "silent auth returned null result! redirecting to authorize with code");
                redirectToAuthorizationEndpoint(req, resp, policy);
            }

        } catch (final Exception ex) {
            Config.logger.log(Level.WARNING, "failed silent auth with exception! redirecting to authorize with code");
            Config.logger.log(Level.WARNING, Arrays.toString(ex.getStackTrace()));
            Config.logger.log(Level.WARNING, ex.getMessage());
            redirectToAuthorizationEndpoint(req, resp, policy);
        }
    }

    private static void redirectToAuthorizationEndpoint(final HttpServletRequest req, final HttpServletResponse resp,
            final String policy) throws Exception {
        AuthorizationRequestUrlParameters parameters;
        final String state = UUID.randomUUID().toString();
        final String nonce = UUID.randomUUID().toString();

        final MsalAuthSession msalAuth = getMsalAuthSession(req.getSession());
        msalAuth.setStateAndNonceAndPolicy(state, nonce, policy);

        final ConfidentialClientApplication client = getConfidentialClientInstance(policy);
        if (policy.equals(EDIT_PROFILE_POLICY) && msalAuth.getAuthenticated()) {
            parameters = AuthorizationRequestUrlParameters.builder(REDIRECT_URI, Collections.singleton(SCOPES))
                    .responseMode(ResponseMode.QUERY).state(state).nonce(nonce).build();
        } else {
            parameters = AuthorizationRequestUrlParameters.builder(REDIRECT_URI, Collections.singleton(SCOPES))
                    .responseMode(ResponseMode.QUERY).prompt(Prompt.SELECT_ACCOUNT).state(state).nonce(nonce).build();
        }

        final String redirectUrl = client.getAuthorizationRequestUrl(parameters).toString();
        Config.logger.log(Level.INFO, "Redirecting user to {0}", redirectUrl);
        resp.setStatus(302);
        resp.sendRedirect(redirectUrl);
    }

    public static void processAADCallback(final HttpServletRequest req, final HttpServletResponse resp)
            throws Exception {
        Config.logger.log(Level.INFO, "processing redirect request...");
        final MsalAuthSession msalAuth = getMsalAuthSession(req.getSession());
      
        try {
            // FIRST, WE MUST VALIDATED THE STATE
            // ***** it is essential for CSRF protection ******
            // if no match, this throws an exception and we stop processing right here:
            final String state = req.getParameter("state");
            validateState(msalAuth, state); 

            // if the state matches, continue, try to interpret any error codes.
            // e.g. redirect to pw reset. this will throw an error & cancel code x-change
            processErrorCodes(req, resp);
            
            // if no errors in request, continue to try to process auth code x-change:
            final String authCode = req.getParameter("code");
            Config.logger.log(Level.FINE, "request code param is {0}", authCode);
            if (authCode == null)  // if no auth code, error out:
                throw new Exception("Auth code is not in request!");
            
            // if auth code exists, proceed to exchange for token:
            Config.logger.log(Level.INFO, "Received AuthCode! Processing Auth code exchange...");
            
            // build the auth code params:
            final AuthorizationCodeParameters authParams = AuthorizationCodeParameters
                .builder(authCode, new URI(REDIRECT_URI)).scopes(Collections.singleton(SCOPES)).build();

            // The exchange token client policy will depend on which policy originated this callback.
            // It was previously stored in the session upon the /authorize request 
            // and must be extracted from the session.
            final String policy = msalAuth.getPolicy();
            Config.logger.log(Level.INFO, "session policy is {0}", policy);

            // Get a client instance and leverage it to acquire the token:
            final ConfidentialClientApplication client = AuthHelper
                    .getConfidentialClientInstance(policy);
            final Future<IAuthenticationResult> future = client.acquireToken(authParams);
            final IAuthenticationResult result = future.get();

            // parse IdToken claims from the IAuthenticationResult:
            parseJWTClaimsSetAndStoreResultInSession(msalAuth, result, client.tokenCache().serialize());

            // if nonce is invalid, stop immediately! this could be a token replay!
            // if validation fails, throws exception and cancels auth:
            validateNonce(msalAuth);

            // set user to authenticated:
            processSuccessfulAuthentication(msalAuth);

            // have they done an edit profile or a password reset? Handle it by re-authorizing
            handlePolicyChange(req, resp, msalAuth);
        } catch (final AADPasswordResetException pre) {
            // this is a password reset request.
            // unlike *unexpected redirect endpoint exceptions*  DO NOT clear the session on PW reset
            // because the session contains important state and nonce info from original request
            Config.logger.log(Level.WARNING, "redirect endpoint requests password reset");
            AuthHelper.passwordReset(req, resp);
            return;
        } catch (final Exception ex) {
            Config.logger.log(Level.WARNING, "Unable to exchange auth code for token");
            Config.logger.log(Level.WARNING, ex.getMessage());
            Config.logger.log(Level.WARNING, Arrays.toString(ex.getStackTrace()));
            req.getSession().invalidate(); // clear the session since there was a problem
        } 
        Config.logger.log(Level.INFO, "redirecting to home page.");
        resp.setStatus(302);
        resp.sendRedirect(HOME_PAGE);
    }

    /**
     *  User needs to be signed out and signed back in on password reset and profile edit
     * @param req
     * @param resp
     * @param msalAuth
     * @throws Exception
     */
    private static void handlePolicyChange(final HttpServletRequest req, final HttpServletResponse resp, final MsalAuthSession msalAuth) throws Exception {
        String acrClaim = msalAuth.getIdTokenClaims().get("acr");
        acrClaim = String.format("%s/", acrClaim);

        if (acrClaim.equals(EDIT_PROFILE_POLICY) || acrClaim.equals(PW_RESET_POLICY)) {
            redirectToSignoutEndpoint(req, resp);
        }
    }

    private static void validateState(final MsalAuthSession msalAuth, final String stateFromRequest) throws Exception{
        Config.logger.log(Level.INFO, "validating state...");        

        final String sessionState = msalAuth.getState();
        final Date now = new Date();
        Config.logger.log(Level.FINE, "session state is: {0} \n request state param is: {1}", new String[] {sessionState, stateFromRequest});

        // if state is null or doesn't match or TTL expired, throw exception
        if (sessionState == null || stateFromRequest == null || !sessionState.equals(stateFromRequest)
                || msalAuth.getStateDate().before(new Date(now.getTime() - (STATE_TTL * 1000)))) {
            throw new Exception("State mismatch or null or empty or expired on validateState!");
        }
        Config.logger.log(Level.INFO, "confirmed that state is valid and matches!");
        msalAuth.setState(null); // don't allow re-use of state
    }

    private static void processErrorCodes(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
        final String errorDescription = req.getParameter("error_description");
        Config.logger.log(Level.INFO, "error description is {0}", errorDescription);
        //if there is an error & its description has password reset err code, do reset pw flow
        if (errorDescription != null) {
            if (errorDescription.contains(AADPasswordResetException.FORGOT_PASSWORD_ERROR_CODE)){
                throw new AADPasswordResetException("Password reset error code in request.");
            }
            throw new Exception("Unknown error in request.");
        }
    }

    private static void parseJWTClaimsSetAndStoreResultInSession(final MsalAuthSession msalAuth, final IAuthenticationResult result, final String serializedTokenCache) throws Exception {
        Config.logger.log(Level.INFO, "placing JWT claims set from auth result into session...");

        final SignedJWT idToken = SignedJWT.parse(result.idToken());
        final JWTClaimsSet jcs = idToken.getJWTClaimsSet();
        msalAuth.setIdTokenClaims(jcs.getClaims());

        msalAuth.setAuthResult(result);
        msalAuth.setTokenCache(serializedTokenCache);
        
        Config.logger.log(Level.INFO, "placed JWT claims set into session");
    }

    private static void validateNonce(final MsalAuthSession msalAuth) throws Exception {
        Config.logger.log(Level.INFO, "validating nonce...");

        final String nonceClaim = msalAuth.getIdTokenClaims().get("nonce");
        final String sessionNonce = msalAuth.getNonce();

        Config.logger.log(Level.FINE, "session nonce is: {0} \n nonce claim in token is: {1}", new String[] {sessionNonce, nonceClaim});
        if (sessionNonce == null || !sessionNonce.equals(nonceClaim)) {
            throw new Exception("Nonce validation failed!");
            
        }
        Config.logger.log(Level.INFO, "confirmed that nonce is valid and matches!");
        msalAuth.setNonce(null); // don't allow re-use of nonce
    }

    private static void processSuccessfulAuthentication(final MsalAuthSession msalAuth) {
        Config.logger.log(Level.INFO, "processing successful auth into session");

        msalAuth.setAuthenticated(true);
        msalAuth.setUsername(msalAuth.getIdTokenClaims().get("name"));

        Config.logger.log(Level.INFO, "successfully placed auth into session");
    }
}
