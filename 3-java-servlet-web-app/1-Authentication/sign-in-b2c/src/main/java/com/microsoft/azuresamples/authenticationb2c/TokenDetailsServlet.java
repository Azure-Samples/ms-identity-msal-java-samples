package com.microsoft.azuresamples.authenticationb2c;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@WebServlet(name = "TokenDetailsServlet", urlPatterns = "/auth_token_details")
public class TokenDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {        
        final HashMap<String,String> filteredClaims = filterClaims(req);

        req.setAttribute("claims", filteredClaims);
        req.setAttribute("bodyContent", "auth/token.jsp");
        final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
        view.forward(req, resp);
    }

    private HashMap<String,String> filterClaims(HttpServletRequest request) {
        MsalAuthSession msalAuth = MsalAuthSession.getMsalAuthSession(request.getSession());

        final String[] exClaims = {"iat", "exp", "nbf", "uti", "aio"};
        final List<String> excludeClaims = Arrays.asList(exClaims);

        HashMap<String,String> filteredClaims = new HashMap<>();
        msalAuth.getIdTokenClaims().forEach((k,v) -> {
            if (!excludeClaims.contains(k))
                filteredClaims.put(k, v);
        });
        return filteredClaims;
    }
}
