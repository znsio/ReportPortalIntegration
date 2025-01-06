package com.znsio.reportportal.integration.properties;

import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.utils.properties.PropertiesLoader;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import com.znsio.reportportal.integration.utils.commandline.CommandLineExecutor;
import com.znsio.reportportal.integration.utils.commandline.CommandLineResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static com.znsio.reportportal.integration.utils.OverriddenVariable.getOverriddenStringValue;

public class ReportPortalPropertiesOverloader {
    private static final Logger LOGGER = LogManager.getLogger(ReportPortalPropertiesOverloader.class.getName());
    private static final String NOT_SET = "not-set";
    private static final String RP_PREFIX = "RP_";
    private static final Properties config = Config.loadProperties(System.getProperty("RP_CONFIG"));
    private static ListenerParameters parameters = new ListenerParameters(PropertiesLoader.load());
    private static Set<ItemAttributesRQ> itemAttributesRQSet = new HashSet<>();
    private static String logMessage = "\n\n" +
                                       "--------------------------------\n" +
                                       "Use ReportPortal configuration: \n" +
                                       "--------------------------------\n";

    public static ListenerParameters getProperties() {
        setLaunchName();
        setSystemAttributes();
        setTestAttributes();
        setCIExecutionAttributes();
        setCustomRPAttributesFromPropertiesFile();
        setRPAttributesFromEnvVariables();
        setRPAttributesFromSystemProperties();
        setLaunchDescription();
        LOGGER.info(logMessage + "\n--------------------------------\n");
        parameters.setAttributes(itemAttributesRQSet);
        return parameters;
    }

    private static void setCustomRPAttributesFromPropertiesFile() {
        config.keySet().stream()
                .map(Object::toString) // Ensure keys are strings
                .filter(key -> key.startsWith(RP_PREFIX)) // Filter by prefix
                .forEach(key -> {
                    String value = config.getProperty(key);
                    addAttributeIfAvailable(getKeyWithoutPrefix(key), value);
                });
    }

    private static void setLaunchDescription() {
        String description = getOverriddenStringValue(Config.DESCRIPTION, config.getProperty(Config.DESCRIPTION));
        addToLogMessage("Provided launch description: " + description);
        if (null != description) {
            addToLogMessage("Add custom launch description: " + description);
            parameters.setDescription(description);
        }
    }

    private static void addToLogMessage(String message) {
        logMessage += "\n" + message;
    }

    private static void setLaunchName() {
        String launchName = getLaunchName();
        if (null == launchName) {
            String testNameProperty = System.getProperty("test");
            if (null != testNameProperty) {
                launchName = testNameProperty;
                addToLogMessage("Running a test. Use test name (" + launchName + ") as launch name");
            } else {
                launchName = new File(System.getProperty("user.dir")).getName();
                addToLogMessage("Use current directory name (" + launchName + ") as the launch name");
            }
        }
        addToLogMessage("Launch Name: " + launchName);
        parameters.setLaunchName(launchName);
    }

    private static String getLaunchName() {
        return getOverriddenStringValue(Config.LAUNCH_NAME, config.getProperty(Config.LAUNCH_NAME));
    }

    private static void setSystemAttributes() {
        addAttributeIfAvailable("OS", System.getProperty("os.name"));
        addAttributeIfAvailable("Username", System.getProperty("user.name"));
        try {
            if (StringUtils.isNotEmpty(InetAddress.getLocalHost().getHostName())) {
                String hostName = InetAddress.getLocalHost().getHostName();
                addAttributeIfAvailable("HostName", hostName);
            }
        } catch (UnknownHostException ex) {
            LOGGER.warn("Unable to set Report Portal Attributes for HostName: " + ex);
        }
    }

    private static void setTestAttributes() {
        addAttributeIfAvailable("TargetEnvironment", getOverriddenStringValue(Config.IS_VISUAL, config.getProperty(Config.TARGET_ENVIRONMENT)));
        addAttributeIfAvailable("Platform", getOverriddenStringValue(Config.PLATFORM, config.getProperty(Config.PLATFORM)));
        addAttributeIfAvailable("Browser", getOverriddenStringValue(Config.BROWSER, config.getProperty(Config.BROWSER)));
        addAttributeIfAvailable("App", getOverriddenStringValue(Config.APP_PACKAGE_NAME, config.getProperty(Config.APP_PACKAGE_NAME)));
        addAttributeIfAvailable("LocalDeviceExecution", getOverriddenStringValue(Config.IS_LOCAL_DEVICE, config.getProperty(Config.IS_LOCAL_DEVICE)));
        addAttributeIfAvailable("VisualEnabled", getOverriddenStringValue(Config.IS_VISUAL, config.getProperty(Config.IS_VISUAL, "false")));
        addAttributeIfAvailable("AutomationBranch", getOverriddenStringValue(Config.BRANCH_NAME,
                                                                             getOverriddenStringValue(config.getProperty(Config.BRANCH_NAME), getBranchNameUsingGitCommand())));
    }

    private static void setCIExecutionAttributes() {
        addAttributeIfAvailable("RunInCI", getOverriddenStringValue(Config.RUN_IN_CI, config.getProperty(Config.RUN_IN_CI)));
        String buildIDKeyName = getOverriddenStringValue(Config.BUILD_ID, config.getProperty(Config.BUILD_ID));
        if (null!=buildIDKeyName) {
            addAttributeIfAvailable("PipelineExecutionID", getOverriddenStringValue(buildIDKeyName));
        }

        String agentName = getOverriddenStringValue(Config.CI_AGENT_NAME, config.getProperty(Config.CI_AGENT_NAME));
        if (null!=agentName) {
            addAttributeIfAvailable("AgentName", getOverriddenStringValue(agentName));
        }
    }

    private static String getBranchNameUsingGitCommand() {
        //TODO: This currently works only for Mac/Linux. Need to update to make it work for Windows as well
        String[] getBranchNameCommand = new String[]{"git", "rev-parse", "--abbrev-ref", "HEAD"};
        CommandLineResponse response = CommandLineExecutor.execCommand(getBranchNameCommand);
        String branchName = response.getStdOut();
        addToLogMessage(String.format("\tBranch name from git command: '%s': '%s'",
                                  Arrays.toString(getBranchNameCommand), branchName));
        return branchName;
    }

    private static String getBrowserVersion(String browserPath) {
        //TODO: This currently works only for Mac/Linux. Need to update to make it work for Windows as well
        String[] getBrowserVersionCommand = new String[]{browserPath, "-v", "|", "awk", "'{print $2}'"};
        CommandLineResponse response = CommandLineExecutor.execCommand(getBrowserVersionCommand);
        String BrowserVersion = response.getStdOut();
        addToLogMessage(String.format("\tBrowser Version from CLI command: '%s': '%s'",
                                  Arrays.toString(getBrowserVersionCommand), BrowserVersion));
        return BrowserVersion;
    }

    private static void setRPAttributesFromSystemProperties() {
        Properties properties = System.getProperties();
        Set<String> set = properties.stringPropertyNames();
        for (String propkey : set) {
            if (propkey.startsWith(RP_PREFIX)) {
                addAttributeIfAvailable(getKeyWithoutPrefix(propkey), properties.getProperty(propkey));
            }
        }
    }

    private static void setRPAttributesFromEnvVariables() {
        Map<String, String> map = System.getenv();
        for (String envKey : map.keySet()) {
            if (envKey.startsWith(RP_PREFIX)) {
                addAttributeIfAvailable(getKeyWithoutPrefix(envKey), map.get(envKey));
            }
        }
    }

    private static String getKeyWithoutPrefix(String key) {
        return key.substring(RP_PREFIX.length());
    }

    private static void addAttributeIfAvailable(String key, String value) {
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
            addToLogMessage("Adding attribute: key: " + key + ", with value: " + value);
            itemAttributesRQSet.add(new ItemAttributesRQ(key, value));
        }
    }
}
