// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.authservlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azuresamples.msal4j.helpers.AuthException;
import com.microsoft.azuresamples.msal4j.helpers.AuthHelper;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;

/**
 * This class defines the endpoint for processing sign in
 * MSAL Java apps using this sample's paradigm will require this.
 */
@WebServlet(name = "SignInServlet", urlPatterns = "/auth/sign_in")
public class SignInServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(SignInServlet.class.getName());

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            AuthHelper.signIn(new IdentityContextAdapterServlet(req, resp));
        } catch (AuthException ex) {
            logger.log(Level.WARNING, ex.getMessage());
            logger.log(Level.WARNING, Arrays.toString(ex.getStackTrace()));
            logger.log(Level.INFO, "redirecting to error page to display auth error to user.");
            resp.sendRedirect(resp.encodeRedirectURL(String.format(req.getContextPath() + "/auth_error_details?details=%s", ex.getMessage())));
        }
    }
}
