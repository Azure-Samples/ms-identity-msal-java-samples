// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.authservlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azuresamples.msal4j.helpers.Config;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextData;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;

/**
 * This class implements filter All incoming requests go through this. This
 * sample uses this filter to redirect unauthorized clients away from protected
 * routes
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    ProtectedRoutes protectedRoutes = new ProtectedRoutes(Arrays.asList(Config.PROTECTED_ENDPOINTS.split(", ")), null,
            null, null);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        IdentityContextData context = new IdentityContextAdapterServlet(request, response).getContext();
        // let the UI templates know whether user is authenticated
        req.setAttribute("isAuthenticated", context.getAuthenticated());
        // surface username to UI templates
        req.setAttribute("username", context.getUsername());
        // send 401 for unauthorized access to the protected endpoints
        if (!context.getAuthenticated()
                && protectedRoutes.allRoutesSet.stream().anyMatch(request.getServletPath()::equals)) {
            sendToUnauthorizedPage(request, response);
            // check for group-related access requirements and claims if authorized
        } else {
            // not a protected route? continue!
            chain.doFilter(request, response);
        }
    }

    private void sendToUnauthorizedPage(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setAttribute("bodyContent", "content/401.jsp");
        final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
        view.forward(req, resp);
    }

    private class ProtectedRoutes {
        Set<String> allRoutesSet;

        /**
         * @param authRoutes          List<String> with each route to be protected e.g.,
         *                            {"/route-1", "/route-2"}
         * @param groupNamesAndRoutes String array with each element in the
         *                            format:'{group-id} /protected-route-1
         *                            /protected-route-2 /protected-route-N'
         */
        public ProtectedRoutes(List<String> authRoutes, List<String> groupNameAndRoutes, List<String> roleNameAndRoutes,
                Map<String, String> permissionNameLookup) {
            this.allRoutesSet = new HashSet<>();
            this.allRoutesSet.addAll(authRoutes);
        }
    }
}
