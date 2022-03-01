// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.helpers;

import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.Group;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.SignedJWT;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
    private String policy = null;
    private boolean authenticated = false;
    private String username = null;
    private String accessToken = null;
    private String idToken = null;
    private List<String> groups = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
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

    public String getAccessToken() {
        return accessToken;
    }

    public Map<String, Object> getIdTokenClaims() {
        return idTokenClaims;
    }

    public List<String> getGroups() {
        return this.groups;
    }

    public List<String> getRoles() {
        return this.roles;
    }

    public String getTokenCache() {
        return this.tokenCache;
    }

    public String getUsername() {
        return username;
    }

    public boolean getAuthenticated() {
        return authenticated;
    }

    public String getNonce() {
        return this.nonce;
    }

    public String getState() {
        return this.state;
    }

    public String getPolicy() {
        return this.policy;
    }

    public Date getStateDate() {
        return this.stateDate;
    }

    public boolean getGroupsOverage() {
        return this.groupsOverage;
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

    public void setGroups(List<Group> groups) {
        this.groups = new ArrayList<>();

        Iterator<Group> it = groups.iterator();
        while(it.hasNext()){
            this.groups.add(it.next().id);
        }
        setHasChanged(true);
    }

    public void clearIdTokenClaims() {
        this.idTokenClaims = new HashMap<>();
        this.setHasChanged(true);
    }

    public void setTokenCache(final String serializedTokenCache) {
        this.tokenCache = serializedTokenCache;
        this.setHasChanged(true);
    }

    public void setUsername(final String username) {
        this.username = username;
        this.setHasChanged(true);
    }

    public void setAuthenticated(final boolean authenticated) {
        this.authenticated = authenticated;
        this.setHasChanged(true);
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

    // policy is relevant for AAD B2C apps only.
    public void setPolicy(final String policy) {
        this.policy = policy;
        this.setHasChanged(true);
    }

    public void setStateAndNonceAndPolicy(String state, String nonce, String policy) {
        this.state = state;
        this.nonce = nonce;
        this.stateDate = new Date();
        this.policy = policy;
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
        this.idToken = authResult.idToken();
        this.setAccessToken(authResult.accessToken());
        this.tokenCache = serializedTokenCache;
        setIdTokenClaims(this.idToken);
        this.username = (String)this.idTokenClaims.get("name");
        this.authenticated = true;

        this.setHasChanged(true);
    }

    public void setAccount(IAccount account) {
        this.account = account;
        this.setHasChanged(true);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        this.setHasChanged(true);
    }

    private void setGroupsOverage(boolean groupsOverage) {
        this.groupsOverage = groupsOverage;
        this.setHasChanged(true);
    }

}
