// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

import java.util.Date;

import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
// import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/api/date")
    @ResponseBody
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
    public String date(/**BearerTokenAuthentication bearerTokenAuth*/) {
        /** 
        //uncomment the parameter in the function params above and the line below to get access to the principal.
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) bearerTokenAuth.getPrincipal();
        // You can then access attributes of the principal, e.g., attributes (claims), the raw tokenValue, and authorities.
        // For example:
        principal.getAttribute("scp");
        */
        return new DateResponse().toString();
    }

    private class DateResponse {
        private String humanReadable;
        private String timeStamp;

        public DateResponse() {
            Date now = new Date();
            this.humanReadable = now.toString();
            this.timeStamp = Long.toString(now.getTime());
        }

        public String toString() {
            return String.format("{\"humanReadable\": \"%s\", \"timeStamp\": \"%s\"}", humanReadable, timeStamp);
        }
    }
}
