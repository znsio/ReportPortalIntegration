package com.znsio.reportportal.integration.properties;

import java.util.Properties;

public class CustomProperties extends Properties {
    private final Properties upperCaseProperties = new Properties();

    @Override
    public synchronized Object put(Object key, Object value) {
        upperCaseProperties.put(key.toString().toUpperCase(), value);
        return super.put(key, value);
    }

    public String getPropertyByIgnoringCase(String key) {
        return upperCaseProperties.getProperty(key.toUpperCase());
    }

    public String getPropertyByIgnoringCase(String key, String defaultValue) {
        return upperCaseProperties.getProperty(key.toUpperCase(), defaultValue);
    }
}
