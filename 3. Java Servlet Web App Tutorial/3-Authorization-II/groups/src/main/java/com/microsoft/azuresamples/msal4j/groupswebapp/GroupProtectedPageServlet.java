// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.groupswebapp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azuresamples.msal4j.helpers.AuthException;
import com.microsoft.azuresamples.msal4j.helpers.AuthHelper;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;
import com.microsoft.graph.core.ClientException;

/**
 * This class defines the endpoint for showing the graph /me endpoint
 * This is here simply to demonstrate the graph call.
 */
@WebServlet(name = "GroupProtectedPageServlet", urlPatterns = {"/admin_only", "/regular_user"})
public class GroupProtectedPageServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(GroupProtectedPageServlet.class.getName());

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            // re-auth (prefer silently) in case the access token is not valid anymore. this gets latest groups claims.
            IdentityContextAdapterServlet contextAdapter = new IdentityContextAdapterServlet(req, resp);
            AuthHelper.acquireTokenSilently(contextAdapter);
            req.setAttribute("bodyContent", "content/200.jsp");
            final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
            view.forward(req, resp);

        } catch (AuthException|ClientException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            logger.log(Level.WARNING, Arrays.toString(ex.getStackTrace()));
            logger.log(Level.INFO, "redirecting to error page to display auth error to user.");
            try {
                RequestDispatcher rd = req.getRequestDispatcher(String.format("/auth_error_details?details=%s", URLEncoder.encode(ex.getMessage(), "UTF-8")));
                rd.forward(req, resp);
            } catch (Exception except) {
                except.printStackTrace();
            }
        }
    }
}
