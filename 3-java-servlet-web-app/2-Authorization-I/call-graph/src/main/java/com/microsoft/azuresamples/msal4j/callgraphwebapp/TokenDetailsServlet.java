// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.callgraphwebapp;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azuresamples.msal4j.helpers.IdentityContextData;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class defines a page for showing the user their token details
 * This is here only for sample demonstration purposes.
 */
@WebServlet(name = "TokenDetailsServlet", urlPatterns = "/token_details")
public class TokenDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {        
        IdentityContextData context = new IdentityContextAdapterServlet(req, resp).getContext();
        final HashMap<String,String> filteredClaims = filterClaims(context);

        req.setAttribute("claims", filteredClaims);
        req.setAttribute("bodyContent", "content/token.jsp");
        final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
        view.forward(req, resp);
    }

    private HashMap<String,String> filterClaims(IdentityContextData context) {
        final String[] claimKeys = {"sub", "aud", "ver", "iss", "name", "oid", "preferred_username", "nonce", "tid"};
        final List<String> includeClaims = Arrays.asList(claimKeys);

        HashMap<String,String> filteredClaims = new HashMap<>();
        context.getIdTokenClaims().forEach((k,v) -> {
            if (includeClaims.contains(k))
                filteredClaims.put(k, v.toString());
        });
        return filteredClaims;
    }
}
