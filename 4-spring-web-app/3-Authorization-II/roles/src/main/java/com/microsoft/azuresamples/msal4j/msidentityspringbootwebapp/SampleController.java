// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapp;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class SampleController {

    @Autowired
    HttpServletRequest req;

    /**
     * Add HTML partial fragment from /templates/content folder to request and serve base html
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @param req used to determine which endpoint triggered this, in order to display required roles.
     * @param fragment used to determine which partial to put into UI
     */
    private String hydrateUI(Model model, String fragment) {
        String path = req.getServletPath();
        String roles;
        if (path.equals("/regular_user")) {
            roles = "PrivilegedAdmin, RegularUser";
        } else if (path.equals("/admin_only")) {
            roles = "PrivilegedAdmin";
        } else {
            roles = "ROLES UNKNOWN FOR THIS REQUEST";
        }
        model.addAttribute("roles", roles);

        model.addAttribute("bodyContent", String.format("content/%s.html", fragment));

        return "base"; //base.html in /templates folder.
    }

    /**
     *  Sign in status endpoint
     *  The page demonstrates sign-in status. For full details, see the src/main/webapp/content/status.jsp file.
     * 
     * @param model Model used for placing bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(value = {"/", "sign_in_status", "/index"})
    public String status(Model model) {
        return hydrateUI(model, "status");
    }

    /**
     *  Token details endpoint
     *  Demonstrates how to extract and make use of token details
     *  For full details, see method: Utilities.filterclaims(OidcUser principal)
     * 
     * @param model Model used for placing claims param and bodyContent param in request before serving UI.
     * @param principal OidcUser this object contains all ID token claims about the user. See utilities file.
     * @return String the UI.
     */
    @GetMapping(path = "/token_details")
    public String tokenDetails(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("claims", Utilities.filterClaims(principal));
        return hydrateUI(model, "token");
    }

    /**
     *  Admin Only endpoint
     *  Demonstrates how to filter so only users with admin role can access.
     * @param req used to determine which endpoint triggered this, in order to display required roles.
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(path = "/admin_only")
    @PreAuthorize("hasAuthority('APPROLE_PrivilegedAdmin')")
    public String adminOnly(Model model) {
        // method decorator limits access to this endpoint to admin approle only
        return hydrateUI(model, "role");
    }

    /**
     *  Regular User endpoint
     *  Demonstrates how to filter so only users with ONE OF or BOTH of PrivilegedUser OR RegularUser role can access.
     * @param req used to determine which endpoint triggered this, in order to display required roles.
     * @param model Model used for placing user param and bodyContent param in request before serving UI.
     * @return String the UI.
     */
    @GetMapping(path = "/regular_user")
    @PreAuthorize("hasAnyAuthority('APPROLE_PrivilegedAdmin','APPROLE_RegularUser')")
    public String regularUser(Model model) {
        // method decorator limits access to this endpoint to either app role
        return hydrateUI(model, "role");
    }

    /**
     *  handleError - show custom 403 page on failing to meet roles requirements
     * @param model Model used for placing user param and bodyContent param in request before serving UI.

     * @return String the UI.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public String handleError(Model model) {
        return hydrateUI(model, "403");
    }

    // survey endpoint - did the sample address your needs?
    // not an integral a part of this tutorial.
    @GetMapping(path = "/survey")
    public String survey(Model model) {
        return hydrateUI(model, "survey");
    }
}
