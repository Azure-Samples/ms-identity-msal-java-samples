// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class Utilities {
    private Utilities() {
        throw new IllegalStateException("Utility class. Don't instantiate");
    }

    /**
     * Take a subset of ID Token claims and put them into KV pairs for UI to display.
     * @param principal OidcUser (see SampleController for details)
     * @return Map of filteredClaims
     */
    public static Map<String,String> filterClaims(OidcUser principal) {
        final String[] claimKeys = {"sub", "aud", "ver", "iss", "name", "oid", "preferred_username"};
        final List<String> includeClaims = Arrays.asList(claimKeys);

        Map<String,String> filteredClaims = new HashMap<>();
        includeClaims.forEach(claim -> {
            if (principal.getIdToken().getClaims().containsKey(claim)) {
                filteredClaims.put(claim, principal.getIdToken().getClaims().get(claim).toString());
            }
        });
        return filteredClaims;
    }

    /**
     * Take a few of the User properties obtained from the graph /me endpoint and put them into KV pairs for UI to display.
     * @param graphAuthorizedClient OAuth2AuthorizedClient created by AAD Boot starter. See the SampleController class for details.
     * @return Map<String,String> select Key-Values from User object
     */
    public static Map<String,String> graphUserProperties(OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = Utilities.getGraphServiceClient(graphAuthorizedClient);
        final User user = graphServiceClient.me().buildRequest().get();
        Map<String,String> userProperties = new HashMap<>();

        if (user == null) {
            userProperties.put("Graph Error", "GraphSDK returned null User object.");
        } else {
            userProperties.put("Display Name", user.displayName);
            userProperties.put("Phone Number", user.mobilePhone);
            userProperties.put("City", user.city);
            userProperties.put("Given Name", user.givenName);
        }
        return userProperties;
    }

    /**
     * getGraphServiceClient prepares and returns a graphServiceClient to make API calls to
     * Microsoft Graph. See docs for GraphServiceClient (GraphSDK for Java v3).
     * 
     * Since the app handles token acquisition through AAD boot starter, we can give GraphServiceClient
     * the ability to use this access token when it requires it. In order to do this, we must create a
     * custom AuthenticationProvider (GraphAuthenticationProvider, see below).
     * 
     * 
     * @param graphAuthorizedClient OAuth2AuthorizedClient created by AAD Boot starter. Used to surface the access token.
     * @return GraphServiceClient GraphServiceClient
     */
    
    public static GraphServiceClient getGraphServiceClient(@Nonnull OAuth2AuthorizedClient graphAuthorizedClient) {
        return GraphServiceClient.builder().authenticationProvider(new GraphAuthenticationProvider(graphAuthorizedClient))
                .buildClient();
    }

    /**
     * Sample GraphAuthenticationProvider class. An Authentication provider is required for setting up a
     * GraphServiceClient. This one extends BaseAuthenticationProvider which in turn implements IAuthenticationProvider.
     * This allows using an Access Token provided by Oauth2AuthorizationClient.
     */
    private static class GraphAuthenticationProvider
            extends BaseAuthenticationProvider {

        private OAuth2AuthorizedClient graphAuthorizedClient;

        /**
         * Set up the GraphAuthenticationProvider. Allows accessToken to be
         * used by GraphServiceClient through the interface IAuthenticationProvider
         * 
         * @param graphAuthorizedClient OAuth2AuthorizedClient created by AAD Boot starter. Used to surface the access token.
         */
        public GraphAuthenticationProvider(@Nonnull OAuth2AuthorizedClient graphAuthorizedClient) {
           this.graphAuthorizedClient = graphAuthorizedClient;
        }

        /**
         * This implementation of the IAuthenticationProvider helps injects the Graph access
         * token into the headers of the request that GraphServiceClient makes.
         *
         * @param requestUrl the outgoing request URL
         * @return a future with the token
         */
        @Override
        public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl){
            return CompletableFuture.completedFuture(graphAuthorizedClient.getAccessToken().getTokenValue());
        }
    }
}
