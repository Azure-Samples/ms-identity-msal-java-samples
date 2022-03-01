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

import com.microsoft.azuresamples.msal4j.helpers.AuthHelper;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;

/**
 * This class defines the endpoint for processing sign out
 * MSAL Java apps using this sample's paradigm will require this.
 */
@WebServlet(name = "SignOutServlet", urlPatterns = "/auth/sign_out")
public class SignOutServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(SignOutServlet.class.getName());

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            AuthHelper.signOut(new IdentityContextAdapterServlet(req, resp));
        } catch (Exception ex){
            logger.log(Level.WARNING, "Unable to sign out");
            logger.log(Level.WARNING, ex.getMessage());
            logger.log(Level.FINEST, Arrays.toString(ex.getStackTrace()));
        }
    }
}