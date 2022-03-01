// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.helpers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import com.microsoft.graph.requests.GroupCollectionRequest;
import com.microsoft.graph.requests.GroupCollectionRequestBuilder;
import com.microsoft.graph.models.Group;

/**
 * GraphHelper class handles creating a Graph SDK client (IGraphServiceClient)
 * and has functions for common Microsoft Graph calls
 */
public class GraphHelper {
    private static Logger logger = Logger.getLogger(GraphHelper.class.getName());

    private GraphHelper() {
        throw new IllegalStateException("Utility class - don't instantiate");
    }

    /**
     * getGraphClient prepares and returns a graphServiceClient to make API calls to
     * Graph. See docs for GraphServiceClient (GraphSDK for Java)
     * 
     * * uses contextAdapter to get the latest access token from context
     * -> make sure you're updating AT in context with AuthHelper.acquireTokenSilently() before each API call.
     * 
     * @param contextAdapter IdentityContextAdapter instance of IdentityContextAdapter
     * @return GraphServiceClient IGraphServiceClient
     */
    public static GraphServiceClient getGraphClient(@Nonnull IdentityContextAdapter contextAdapter) {
        return GraphServiceClient.builder().authenticationProvider(new MsalGraphAuthenticationProvider(contextAdapter))
                .buildClient();
    }

    /**
     * Our Msal Graph Authentication Provider class. Required for setting up a
     * GraphServiceClient. It extends BaseAuthenticationProvider which in turn implements IAuthenticationProvider.
     */
    private static class MsalGraphAuthenticationProvider
            extends BaseAuthenticationProvider {

        private IdentityContextAdapter contextAdapter;

        /**
         * Set up the MsalGraphAuthenticationProvider. Allows accessToken to be
         * used by GraphServiceClient through the interface IAuthenticationProvider
         * 
         * uses contextAdapter to get the latest access token from context
         * -> make sure you're updating AT in context with AuthHelper.acquireTokenSilently() before each API call.
         * 
         * @param contextAdapter IdentityContextAdapter for getting your access token for Graph
         */
        public MsalGraphAuthenticationProvider(@Nonnull IdentityContextAdapter contextAdapter) {
           this.contextAdapter = contextAdapter;
        }

        /**
         * This implementation of the IAuthenticationProvider helps injects the Graph access
         * token from Azure AD into the headers of the IHttp request used by GraphSDK.
         * 
         * uses contextAdapter to get the latest access token from context
         * -> make sure you're updating AT in context with AuthHelper.acquireTokenSilently() before each API call.
         *
         * @param requestUrl the outgoing request URL
         * @return a future with the token
         */
        @Override
        public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl){
            return CompletableFuture.completedFuture(contextAdapter.getContext().getAccessToken());
        }
    }

    /**
     * Get groups that the user belongs to from MS Graph
     */
    public static List<Group> getGroups(GraphServiceClient graphClient) {
        // Set up the initial request builder and build request for the first page
        GroupCollectionRequestBuilder groupsRequestBuilder = graphClient.groups();
        GroupCollectionRequest groupsRequest = groupsRequestBuilder.buildRequest().top(999);

        List<Group> allGroups = new ArrayList<>();

        do {
            try {
                // Execute the request
                GroupCollectionPage groupsCollection = groupsRequest.get();

                // Process each of the items in the response
                for (Group group : groupsCollection.getCurrentPage()) {
                    allGroups.add(group);
                }

                // Build the request for the next page, if there is one
                groupsRequestBuilder = groupsCollection.getNextPage();
                if (groupsRequestBuilder == null) {
                    groupsRequest = null;
                } else {
                    groupsRequest = groupsRequestBuilder.buildRequest();
                }

            } catch (ClientException ex) {
                // Handle failure
                logger.log(Level.WARNING, ex.getMessage());
                logger.log(Level.WARNING, Arrays.toString(ex.getStackTrace()));
                groupsRequest = null;
            }

        } while (groupsRequest != null);

        return allGroups;

    }
}
