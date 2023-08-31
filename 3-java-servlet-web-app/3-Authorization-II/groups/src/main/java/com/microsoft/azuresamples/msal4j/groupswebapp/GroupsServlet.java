// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.groupswebapp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azuresamples.msal4j.helpers.IdentityContextData;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;
import com.microsoft.graph.core.ClientException;

/**
 * This class defines the endpoint for showing the user's groups
 * This is here simply to demonstrate the graph call.
 */
@WebServlet(name = "GroupsServlet", urlPatterns = "/groups")
public class GroupsServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(GroupsServlet.class.getName());

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            IdentityContextAdapterServlet contextAdapter = new IdentityContextAdapterServlet(req, resp);
            IdentityContextData context = contextAdapter.getContext();

            List<String> groups = context.getGroups();
            boolean groupsOverage = context.getGroupsOverage(); // for signalling to the user that an overage has happened.
            StringBuilder groupsStringBuilder = new StringBuilder();

            // get 10 of the groups if they exist (for showing the user in UI)
            if (!groups.isEmpty()) {
                Iterator<String> it = groups.iterator();
                for (int i=0; it.hasNext() && i < 10; i++){
                    groupsStringBuilder = groupsStringBuilder.append(it.next()).append(", <br>");
                }
                groupsStringBuilder = groupsStringBuilder.append("...");
            } else {
                groupsStringBuilder = groupsStringBuilder.append("User is not a member of any groups. <br>");
            }

            req.setAttribute("groups", groupsStringBuilder.toString());
            req.setAttribute("groupsNum", context.getGroups().size());
            req.setAttribute("groupsOverage", groupsOverage);
            req.setAttribute("bodyContent", "content/groups.jsp");
            final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
            view.forward(req, resp);

        } catch (ClientException ex) {
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
