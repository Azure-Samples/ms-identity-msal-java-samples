// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.helpers;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.SignedJWT;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class defines all auth-related session properties that are required MSAL
 * Java apps using this sample repository's paradigm will require this.
 */
public class IdentityContextData implements Serializable {
    private static final long serialVersionUID = 2L;
    private String nonce = null;
    private String state = null;
    private Date stateDate = null;

    private String username = null;
    private List<String> groups = new ArrayList<>();
    private final List<String> roles = new ArrayList<>();
    private IAccount account = null;
    private Map<String, Object> idTokenClaims = new HashMap<>();
    private String tokenCache = null;
    private boolean hasChanged = false;
    private boolean groupsOverage = false;

    public boolean hasChanged() {
        return hasChanged;
    }
    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }

    public IAccount getAccount() {
        return account;
    }

    public Map<String, Object> getIdTokenClaims() {
        return idTokenClaims;
    }

    public String getTokenCache() {
        return this.tokenCache;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Checks if an ID Token was obtained from the authentication provider - Microsoft Entra
     * Remarks: this does not check if the Id Token is expired, because when the website received the token,
     * it is guaranteed to be non expired. The session expiration will ensure new id tokens are fetched periodically.
     *
     * @return true if a user is authenticated
     */
    public boolean isAuthenticated() {
        return !idTokenClaims.isEmpty();
    }

    /**
     * Checks if an ID Token was obtained from the authentication provider - Microsoft Entra and if the Id token
     * has proof (a claim) that the user has passed the conditional access associated with this context (e.g. multifactor auth),
     * i.e. if the id token has a claim named "acrs" with the value authenticationContextId
     * Remarks: this does not check if the Id Token is expired, because when the website received the token,
     * it is guaranteed to be non expired. Use session expiration to ensure new id tokens are fetched periodically.
     *
     * @param authenticationContextId - the ID of an authentication context, which Conditional Access policies can refer to
     * @return true if the user is an id token exist and it has an "acrs" claim with value authenticationContextId
     */
    public boolean isAuthenticated(String authenticationContextId) {

        if (authenticationContextId == null || authenticationContextId.isEmpty() ) {
            throw new IllegalArgumentException("authenticationContextId cannot be null");
        }

        if (idTokenClaims.isEmpty()) {
            return false;
        }

        JSONArray acrsClaim = (JSONArray)this.idTokenClaims.get("acrs");
        if (acrsClaim != null) {
            return acrsClaim.stream().anyMatch(authenticationContextId::equals);
        }

        return false;
    }

    public String getNonce() {
        return this.nonce;
    }

    public String getState() {
        return this.state;
    }

    public Date getStateDate() {
        return this.stateDate;
    }

    public void setIdTokenClaims(String rawIdToken) throws ParseException {
        final Map<String, Object> tokenClaims = SignedJWT.parse(rawIdToken).getJWTClaimsSet().getClaims();
        this.idTokenClaims = tokenClaims;
        setGroupsFromIdToken(tokenClaims);
        setRolesFromIdToken(idTokenClaims);
        this.setHasChanged(true);
    }

    public void setGroupsFromIdToken(Map<String,Object> idTokenClaims) {
        JSONArray groupsFromToken = (JSONArray)this.idTokenClaims.get("groups");
        if (groupsFromToken != null) {
            setGroupsOverage(false);
            this.groups = new ArrayList<>();
            groupsFromToken.forEach(elem -> this.groups.add((String)elem));
        } else {
            // check for potential groups overage scenario!
            JSONObject jsonObj = (JSONObject)idTokenClaims.get("_claim_names");
            if (jsonObj != null && jsonObj.containsKey("groups")) {
                // overage scenario exists, handle it:
                setGroupsOverage(true);
            }
        }
        setHasChanged(true);
    }

    public void setRolesFromIdToken(Map<String,Object> idTokenClaims) {
        JSONArray rolesFromToken = (JSONArray)idTokenClaims.get("roles");
        if (rolesFromToken != null) {
            this.groups = new ArrayList<>();
            rolesFromToken.forEach(elem -> this.roles.add((String)elem));
            setHasChanged(true);
        }
    }


    public void setNonce(String nonce) {
        this.nonce = nonce;
        this.setHasChanged(true);
    }

    public void setState(final String state) {
        this.state = state;
        this.stateDate = new Date();
        this.setHasChanged(true);
    }

    public void setStateAndNonce(String state, String nonce) {
        this.state = state;
        this.nonce = nonce;
        this.stateDate = new Date();
        this.setHasChanged(true);
    }

    public void setAuthResult(IAuthenticationResult authResult, String serializedTokenCache)
            throws java.text.ParseException {
        this.setAccount(authResult.account());
        String idToken = authResult.idToken();
        this.setAccessToken(authResult.accessToken());
        this.tokenCache = serializedTokenCache;
        setIdTokenClaims(idToken);
        this.username = (String)this.idTokenClaims.get("name");

        this.setHasChanged(true);
    }

    public void setAccount(IAccount account) {
        this.account = account;
        this.setHasChanged(true);
    }

    public void setAccessToken(String accessToken) {
        this.setHasChanged(true);
    }

    private void setGroupsOverage(boolean groupsOverage) {
        this.groupsOverage = groupsOverage;
        this.setHasChanged(true);
    }

}
