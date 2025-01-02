package com.znsio.reportportal.integration.utils;

public class OverriddenVariable {

    public static String getOverriddenStringValue(String key) {
        return getValueFromEnvOrProperty(key);
    }

    public static String getOverriddenStringValue(String key, String defaultValue) {
        return (isKeyProvidedInEnvOrProperty(key))
               ? getValueFromEnvOrProperty(key)
               : defaultValue;
    }

    private static boolean isKeyProvidedInEnvOrProperty(String key) {
        return (null != System.getenv(key)) || (null != System.getProperty(key));
    }

    private static String getValueFromEnvOrProperty(String key) {
        return (null == System.getProperty(key)) ? System.getenv(key) : System.getProperty(key);
    }
}
