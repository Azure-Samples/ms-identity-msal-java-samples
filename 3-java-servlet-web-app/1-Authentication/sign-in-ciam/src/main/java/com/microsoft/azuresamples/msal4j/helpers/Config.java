// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.helpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads properties file when the servlet starts.
 * MSAL Java apps using this sample repo's paradigm will require this.
 */

public class Config {
    private static Logger logger = Logger.getLogger(Config.class.getName());
    private static Properties props;

    static {
        try {
            props = instantiateProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties instantiateProperties() throws IOException {
        final Properties props = new Properties();
        try {
            props.load(Config.class.getClassLoader().getResourceAsStream("authentication.properties"));
        } catch (final IOException ex) {
            logger.log(Level.SEVERE, "Could not load properties file");
            throw ex;
        }
        return props;
    }

    public static final String AUTHORITY = Config.getProperty("aad.authority");
    public static final String CLIENT_ID = Config.getProperty("aad.clientId");
    public static final String SECRET = Config.getProperty("aad.secret");
    public static final String SCOPES = Config.getProperty("aad.scopes");
    public static final String SIGN_OUT_ENDPOINT = Config.getProperty("aad.signOutEndpoint");
    public static final String POST_SIGN_OUT_FRAGMENT = Config.getProperty("aad.postSignOutFragment");
    public static final Long STATE_TTL = Long.parseLong(Config.getProperty("app.stateTTL"));
    public static final String HOME_PAGE = Config.getProperty("app.homePage");
    public static final String REDIRECT_ENDPOINT = Config.getProperty("app.redirectEndpoint");
    public static final String REDIRECT_URI = String.format("%s%s", HOME_PAGE, REDIRECT_ENDPOINT);
    public static final String SESSION_PARAM = Config.getProperty("app.sessionParam");
    public static final String PROTECTED_ENDPOINTS = Config.getProperty("app.protect.authenticated");

    public static String getProperty(final String key) {
        String prop;

        try {
            prop = Config.props.getProperty(key);

            if (prop != null) {
                Config.logger.log(Level.FINE, "{0} is {1}", new String[]{key, prop});
                return prop;
            } else {
                Config.logger.log(Level.SEVERE, "A key could not be loaded from the properties file: {0}", key);
                return "";
            }
        } catch (Exception ex) {
            Config.logger.log(Level.SEVERE, "Could not load required key {0} from config", key);
            throw ex;
        }
    }
}