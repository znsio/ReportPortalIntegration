package com.znsio.reportportal.integration.properties;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static final String TARGET_ENVIRONMENT = "TARGET_ENVIRONMENT";
    public static final String PLATFORM = "PLATFORM";
    public static final String LAUNCH_NAME = "LAUNCH_NAME";
    public static final String IS_VISUAL = "IS_VISUAL";
    public static final String IS_LOCAL_DEVICE = "IS_LOCAL_DEVICE";
    public static final String BROWSER = "BROWSER";
    public static final String APP_PACKAGE_NAME = "APP_PACKAGE_NAME";
    public static final String RUN_IN_CI = "RUN_IN_CI";
    public static final String BUILD_ID = "BUILD_ID";
    public static final String AGENT_NAME = "AGENT_NAME";
    public static final String BRANCH_NAME = "BRANCH_NAME";
    public static final String DESCRIPTION = "DESCRIPTION";
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    @NotNull
    public static Properties loadProperties(String configFile) {
        if (null == configFile) {
            configFile = "src/test/resources/reportportal.properties";
        }
        String configFilePath = System.getProperty("user.dir") + File.separator + configFile;
        if (new File(configFilePath).exists()) {
            LOGGER.info("Using config file: " + configFilePath);
            final Properties properties;
            try (InputStream input = new FileInputStream(configFilePath)) {
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
        } else {
            throw new RuntimeException("ERROR: Config file not found: " + configFile + "\n");
        }
    }
}
