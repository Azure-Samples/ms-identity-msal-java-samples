// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.msalwebsample;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("aad")
class BasicConfiguration {
    String clientId;
    String authority;
    String redirectUri;
    String logoutRedirectUri;
    String secretKey;
    String oboDefaultScope;

    public String getClientId() {
        return clientId;
    }

    String getAuthority() {
        if (!authority.endsWith("/")) {
            authority += "/";
        }
        return authority;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getLogoutRedirectUri() {
        return logoutRedirectUri;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getOboDefaultScope() {
        return oboDefaultScope;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setOboDefaultScope(String oboDefaultScope) {
        this.oboDefaultScope = oboDefaultScope;
    }

    public void setLogoutRedirectUri(String logoutRedirectUri) {
        this.logoutRedirectUri = logoutRedirectUri;
    }
}