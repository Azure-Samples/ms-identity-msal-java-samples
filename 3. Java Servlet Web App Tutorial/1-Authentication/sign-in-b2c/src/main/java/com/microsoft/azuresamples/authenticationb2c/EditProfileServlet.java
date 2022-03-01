package com.microsoft.azuresamples.authenticationb2c;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "EditProfileServlet", urlPatterns = "/auth_edit_profile")
public class EditProfileServlet extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            AuthHelper.editProfile(req, resp);
        } catch (Exception ex){
            Config.logger.log(Level.WARNING, "Unable to redirect browser to edit profile endpoint");
            Config.logger.log(Level.WARNING, ex.getMessage());
            Config.logger.log(Level.FINEST, Arrays.toString(ex.getStackTrace()));
        }
    }
}
