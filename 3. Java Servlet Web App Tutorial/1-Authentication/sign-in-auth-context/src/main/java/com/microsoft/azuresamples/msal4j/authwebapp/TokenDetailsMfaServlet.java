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

@WebServlet(name = "TokenDetailsMfaServlet", urlPatterns = {"/token_details_mfa"})
public class TokenDetailsMfaServlet extends HttpServlet {

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
