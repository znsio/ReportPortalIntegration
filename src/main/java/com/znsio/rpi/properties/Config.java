package com.znsio.rpi.properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Config {
    static final String TARGET_ENVIRONMENT = "TARGET_ENVIRONMENT";
    static final String PLATFORM = "PLATFORM";
    static final String APP_NAME = "APP_NAME";
    static final String IS_VISUAL = "IS_VISUAL";
    static final String IS_MOBILAB = "IS_MOBILAB";
    static final String BROWSER = "BROWSER";
    static final String APP_PACKAGE_NAME = "APP_PACKAGE_NAME";
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    private Config() {
        LOGGER.debug("Setup - private constructor");
    }

    @NotNull
    static Properties loadProperties(String configFile) {
        final Properties properties;
        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + File.separator + configFile)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.info("ERROR: Config file not found, or unable to read it: "
                    + configFile + "\n");
            LOGGER.debug(ExceptionUtils.getStackTrace(ex));
            throw new RuntimeException("ERROR: Config file not found, or unable to read it: "
                    + configFile + "\n", ex);
        }
        return properties;
    }
}
