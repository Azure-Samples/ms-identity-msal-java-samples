package com.microsoft.azuresamples.authenticationb2c;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

@WebServlet(name = "AADRedirectServlet", urlPatterns = "/auth_redirect")
public class AADRedirectServlet extends HttpServlet {
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        Config.logger.log(Level.FINE, "Request has come with params {0}", req.getQueryString());
        try {
            AuthHelper.processAADCallback(req, resp);
        } catch (Exception e) {
            Config.logger.log(Level.WARNING, "Unable to process getting token by Auth Code: /auth_redirect endpoint");
            Config.logger.log(Level.WARNING, e.getMessage());
            Config.logger.log(Level.FINEST, Arrays.toString(e.getStackTrace()));
            
        }
    }

}
