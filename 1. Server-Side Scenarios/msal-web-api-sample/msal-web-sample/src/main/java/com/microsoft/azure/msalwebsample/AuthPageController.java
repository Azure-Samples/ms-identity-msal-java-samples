// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azure.msalwebsample;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;

import static com.microsoft.azure.msalwebsample.AuthHelper.getAuthSessionObject;

@Controller
public class AuthPageController {

    @Autowired
    AuthHelper authHelper;

    @RequestMapping("/msal4jsample")
    public String homepage() {
        return "index";
    }

    @RequestMapping("/msal4jsample/secure/aad")
    public ModelAndView securePage(HttpServletRequest httpRequest) throws ParseException {
        ModelAndView mav = new ModelAndView("auth_page");

        setAccountInfo(mav, httpRequest);
        return mav;
    }

    @RequestMapping("/msal4jsample/sign_out")
    public void signOut(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        httpRequest.getSession().invalidate();

        httpResponse.sendRedirect(AuthHelper.END_SESSION_ENDPOINT +
                "?post_logout_redirect_uri=" + URLEncoder.encode(authHelper.getLogoutRedirectUrl(), "UTF-8"));
    }

    @RequestMapping("/obo_api")
    public ModelAndView callOboApi(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {

        ModelAndView mav = new ModelAndView("auth_page");
        String oboApiCallResult = null;
        try {
            setAccountInfo(mav, httpRequest);

            IAuthenticationResult result = authHelper.getAuthResultBySilentFlow(httpRequest, authHelper.configuration.oboDefaultScope);
            oboApiCallResult = callOboService(result.accessToken());
        } catch (Exception ex) {
            authHelper.removePrincipalFromSession(httpRequest);
            httpResponse.setStatus(500);
            httpRequest.setAttribute("error", ex.getMessage());
            httpRequest.getRequestDispatcher("/error").forward(httpRequest, httpResponse);
        }

        mav.addObject("obo_api_call_res", oboApiCallResult);
        return mav;
    }

    private void setAccountInfo(ModelAndView model, HttpServletRequest httpRequest) throws ParseException {
        IAuthenticationResult auth = getAuthSessionObject(httpRequest);

        String tenantId = JWTParser.parse(auth.idToken()).getJWTClaimsSet().getStringClaim("tid");

        model.addObject("tenantId", tenantId);
        model.addObject("account", getAuthSessionObject(httpRequest).account());
    }

    private String callOboService(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        return restTemplate.exchange("http://localhost:8081/graphMeApi", HttpMethod.GET,
                entity, String.class).getBody();
    }
}
