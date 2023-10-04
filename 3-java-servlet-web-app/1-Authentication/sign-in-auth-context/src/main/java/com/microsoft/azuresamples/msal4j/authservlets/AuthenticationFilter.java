// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.authservlets;

import java.io.IOException;
import java.util.*;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azuresamples.msal4j.helpers.*;

/**
 * This class implements filter All incoming requests go through this. This
 * sample uses this filter to redirect unauthorized clients away from protected
 * routes
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    Set<String> routesThatNeedAuthentication = new HashSet<>(Collections.singletonList(Config.PROTECTED_ENDPOINTS));
    Set<String> routesThatNeedAuthenticationAndMFA = new HashSet<>(Collections.singletonList(Config.PROTECTED_MFA_ENDPOINTS));


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        IdentityContextAdapter context = new IdentityContextAdapterServlet(request, response);

        boolean isAuthenticated = context.getContext().isAuthenticated();
        boolean isAutheticatedAndMfa = context.getContext().isAuthenticated(Config.AUTHENTICATION_CONTEXT_ID_MFA);

        // let the UI templates know whether user is authenticated
        req.setAttribute("isAuthenticated", isAuthenticated);
        req.setAttribute("isAutheticatedAndMfa", isAutheticatedAndMfa);
        req.setAttribute("username", context.getContext().getUsername());


        String route = request.getServletPath();
        if (routesThatNeedAuthentication.stream().anyMatch(route::equals) && !isAuthenticated) {
            // send 401 for unauthorized access to the protected endpoints
            // user can sing-in via the SingIn button / servlet
            sendToUnauthorizedPage(request, response);
        } else if (routesThatNeedAuthenticationAndMFA.stream().anyMatch(route::equals) && !isAutheticatedAndMfa) {
            // Challenge the user directly by redirecting them to the Identity Provider without showing the 401.
            AuthHelper.challengeUser(context, Config.AUTHENTICATION_CONTEXT_ID_MFA);
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

}
