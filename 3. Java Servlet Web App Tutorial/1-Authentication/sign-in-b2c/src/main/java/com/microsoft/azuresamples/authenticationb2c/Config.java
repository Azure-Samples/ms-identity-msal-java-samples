package com.microsoft.azuresamples.authenticationb2c;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class Config implements ServletContextListener {
    public static Logger logger = Logger.getLogger("Logger");
    private static Properties props = Config.instantiateProperties();

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        Config.logger.setLevel(Level.FINEST);
        Config.logger.log(Level.INFO, "APPLICATION IS RUNNING ON http://SERVER-IP:PORT{0}/index",
                event.getServletContext().getContextPath());
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        Config.logger.log(Level.INFO, "EXITING.");
    }

    private static Properties instantiateProperties() {
        final Properties props = new Properties();
        try {
            props.load(Config.class.getClassLoader().getResourceAsStream("authentication.properties"));
        } catch (final IOException ex) {
            ex.printStackTrace();
            Config.logger.log(Level.SEVERE, "Could not load properties file. Exiting");
            Config.logger.log(Level.SEVERE, Arrays.toString(ex.getStackTrace()));
            System.exit(1);
            return null;
        }
        return props;
    }

    public static String getProperty(final String key) {
        String prop = null;
        if (props != null) {
            prop = Config.props.getProperty(key);
            if (prop != null) {
                Config.logger.log(Level.FINE, "{0} is {1}", new String[] { key, prop });
                return prop;
            } else {
                Config.logger.log(Level.SEVERE, "Could not load {0}! EXITING!", key);
                System.exit(1);
                return null;
            }
        } else {
            Config.logger.log(Level.SEVERE, "Could not load property reader! EXITING!");
            System.exit(1);
            return null;
        }
    }

}
