// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.authwebapp;

import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextData;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class defines a page for showing the user their token details
 * This is here only for sample demonstration purposes.
 */
@WebServlet(name = "TokenDetailsServlet", urlPatterns = {"/token_details"})
public class TokenDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {        
        IdentityContextData context = new IdentityContextAdapterServlet(req, resp).getContext();

        // All ID Token claims
        req.setAttribute("claims", context.getIdTokenClaims());
        req.setAttribute("bodyContent", "content/token.jsp");
        final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
        view.forward(req, resp);
    }
}

