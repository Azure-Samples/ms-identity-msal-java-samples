// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.groupswebapp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class defines a page for showing the user AAD error details
 *  In a real world app, you should likely NOT want to give out error details to your users.
 */
@WebServlet(name = "AuthErrorDetailsServlet", urlPatterns = "/auth_error_details")
public class AuthErrorDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final String details = req.getParameter("details");

        req.setAttribute("details", details);
        req.setAttribute("bodyContent", "content/500.jsp");
        final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
        view.forward(req, resp);
    }
}