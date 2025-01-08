package com.znsio.reportportal.integration.properties;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConfigTest {

    @Test
    public void testLoadProperties() {
        CustomProperties loadedProperties = Config.loadProperties("src/test/resources/reportportal.properties");
        assertNotNull(loadedProperties);
        assertEquals(loadedProperties.getPropertyByIgnoringCase("RP_FOO"), "bar");
        assertEquals(loadedProperties.getPropertyByIgnoringCase("rp_foo"), "bar");
        assertEquals(loadedProperties.getPropertyByIgnoringCase("Rp_Foo"), "bar");
        assertEquals(loadedProperties.getPropertyByIgnoringCase("Foo"), null);
    }

}
