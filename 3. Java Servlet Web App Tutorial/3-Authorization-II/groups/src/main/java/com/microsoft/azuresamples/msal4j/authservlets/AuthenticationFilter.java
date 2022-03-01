// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.authservlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.azuresamples.msal4j.helpers.Config;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextData;
import com.microsoft.azuresamples.msal4j.helpers.IdentityContextAdapterServlet;

/**
 * This class implements filter All incoming requests go through this. This
 * sample uses this filter to redirect unauthorized clients away from protected
 * routes
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    Map<String,String> groupNameIdLookup = parseGroupNamesAndIds(Config.GROUP_NAMES_AND_IDS);

    ProtectedRoutes protectedRoutes = new ProtectedRoutes(
        Arrays.asList(Config.PROTECTED_ENDPOINTS.split(", ")),
        Arrays.asList(Config.GROUPS_PROTECTED_ENDPOINTS.split(", ")),
        null,
        groupNameIdLookup);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        IdentityContextData context = new IdentityContextAdapterServlet(request, response).getContext();
        // send 401 for unauthorized access to the protected endpoints
        if (!context.getAuthenticated() && protectedRoutes.allRoutesSet.stream().anyMatch(request.getServletPath()::equals)) {
                sendToUnauthorizedPage(request, response);
        // check for group-related access requirements and claims if authorized
        } else {
            Set<String> routeRequiresGroup = protectedRoutes.requireGroup.get(request.getServletPath());
            if (!idTokenHasRequiredGroup(routeRequiresGroup, context.getGroups())) {
                sendToForbiddenPage(request, response);
            } else {
                // not a protected route? continue!
                chain.doFilter(request, response);
            }
        }
    }

    private void sendToUnauthorizedPage(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setAttribute("bodyContent", "content/401.jsp");
        final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
        view.forward(req, resp);
    }

    private void sendToForbiddenPage(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setAttribute("bodyContent", "content/403.jsp");
        final RequestDispatcher view = req.getRequestDispatcher("index.jsp");
        view.forward(req, resp);
    }

    private boolean idTokenHasRequiredGroup(Set<String> routeRequiresGroup, List<String> idTokenGroups) {
        if (routeRequiresGroup == null || routeRequiresGroup.isEmpty())
            return true;
        if (idTokenGroups.isEmpty())
            return false;
        return idTokenGroups.stream().anyMatch(routeRequiresGroup::contains);
    }

    private Map<String,String> parseGroupNamesAndIds(String groupNamesAndIds) {
        String [] groupAndId = groupNamesAndIds.split(", ");
        Map<String,String> groupsIds = new HashMap<>();
        for (int x=0; x < groupAndId.length; x++) {
            String [] KV = groupAndId[x].split(" ");
            groupsIds.put(KV[0], KV[1]);
        }
        return groupsIds;
    }
}

class ProtectedRoutes {
    Set<String> allRoutesSet;
    Map<String,Set<String>> requireGroup; // a map of routes. each one is /path containing followed by required groups
    Map<String,Set<String>> requireRole; // same as above
    /**
     * @param authRoutes          List<String> with each route to be protected e.g.,
     *                            {"/route-1", "/route-2"}
     * @param groupNamesAndRoutes String array with each element in the
     *                            format:'{group-id} /protected-route-1
     *                            /protected-route-2 /protected-route-N'
     */
    public ProtectedRoutes(List<String> authRoutes, List<String> groupNameAndRoutes, List<String> roleNameAndRoutes, Map<String,String> groupNameLookup) {
        this.allRoutesSet = new HashSet<>();
        this.allRoutesSet.addAll(authRoutes);
        this.requireGroup = parse(groupNameAndRoutes, groupNameLookup);
        this.requireRole = parse(roleNameAndRoutes, groupNameLookup);
    }

    /**
     * Parses a String array from the format (LOGICAL OR when more than one group):
     * '/protected-route group-1 group-2 group-N'
     * or for roles:
     * '/protected-route role-1 role-2 role-N'
     * ALSO ADDS TO THE LIST OF ALL ROUTES PROTECTED (allRoutesSet)
     * @param routeAndRequiredGroupOrRole (String array)
     * @return
     */
    private Map<String,Set<String>> parse(List<String> routeAndRequiredGroupOrRole, Map<String,String> groupNameLookup){

        Map<String,Set<String>> parsedRoutes = new HashMap<>();
        if (routeAndRequiredGroupOrRole == null)
            return parsedRoutes;

        Iterator<String> it = routeAndRequiredGroupOrRole.iterator();
        while (it.hasNext()) {
            String [] parts = it.next().split(" ");
            String route = parts[0];
            Set<String> groupOrRoleSet = parsedRoutes.get(route);
            if (groupOrRoleSet == null) {
               groupOrRoleSet = new HashSet<>();
            }

            // get all the named group/role that this route wants,
            // translate them to groupID/roleId, and put them in 
            for (int x = 1; x < parts.length; x++) {
                if (groupNameLookup != null) {
                    groupOrRoleSet.add(groupNameLookup.get(parts[x]));
                } else {
                    groupOrRoleSet.add(parts[x]);
                }
            }
            this.allRoutesSet.add(route);
            parsedRoutes.put(route, groupOrRoleSet);
        }
        return parsedRoutes;
    }
}