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

    public static ListenerParameters getProperties() {
        setLaunchName();
        setSystemAttributes();
        setTestAttributes();
        setPipelineAttributes();
        setRPAttributesFromSystemVariables();
        setLaunchDescription();
        parameters.setAttributes(itemAttributesRQSet);
        return parameters;
    }

    private static void setLaunchDescription() {
        String description = getOverriddenStringValue(Config.DESCRIPTION, config.getProperty(Config.DESCRIPTION));
        System.out.println("Provided launch description: " + description);
        if (null != description) {
            System.out.println("Add custom launch description: " + description);
            parameters.setDescription(description);
        }
    }

    private static void setLaunchName() {
        String launchName = getLaunchName();
        if (null == launchName) {
            String testNameProperty = System.getProperty("test");
            if (null != testNameProperty) {
                launchName = testNameProperty;
                System.out.println("Running a test. Use test name (" + launchName + ") as launch name");
            } else {
                launchName = new File(System.getProperty("user.dir")).getName();
                System.out.println("Use current directory name (" + launchName + ") as the launch name");
            }
        }
        System.out.println("Launch Name: " + launchName);
        parameters.setLaunchName(launchName);
    }

    private static String getPlatform() {
        return (null == config.getProperty(Config.PLATFORM)) ? NOT_SET : config.getProperty(Config.PLATFORM).toUpperCase();
    }

    private static String getLaunchName() {
        return getOverriddenStringValue(Config.LAUNCH_NAME, config.getProperty(Config.LAUNCH_NAME));
    }

    private static void setSystemAttributes() {
        addAttributes("OS", System.getProperty("os.name"));
        addAttributes("Username", System.getProperty("user.name"));
        try {
            if (StringUtils.isNotEmpty(InetAddress.getLocalHost().getHostName())) {
                String hostName = InetAddress.getLocalHost().getHostName();
                addAttributes("HostName", hostName);
            }
        } catch (UnknownHostException ex) {
            LOGGER.warn("Unable to set Report Portal " +
                        "Attributes for HostName: " + ex);
        }
    }

    private static void setTestAttributes() {
        addAttributes("TargetEnvironment", config.getProperty(Config.TARGET_ENVIRONMENT));
        addAttributes("Platform", getPlatform());
        if (isPlatformWeb()) {
            addAttributes("Browser", config.getProperty(Config.BROWSER));
        } else {
            addAttributes("App", config.getProperty(Config.APP_PACKAGE_NAME));
            addAttributes("LocalDeviceExecution", config.getProperty(Config.IS_LOCAL_DEVICE));
        }
        addAttributes("VisualEnabled", getOverriddenStringValue(Config.IS_VISUAL,
                                                                config.getProperty(Config.IS_VISUAL, "false")));
        addAttributes("AutomationBranch", getOverriddenStringValue(Config.BRANCH_NAME,
                                                                   getOverriddenStringValue(config.getProperty(Config.BRANCH_NAME), getBranchNameUsingGitCommand())));
    }

    private static void setPipelineAttributes() {
        if (Boolean.parseBoolean(getOverriddenStringValue(Config.RUN_IN_CI, config.getProperty(Config.RUN_IN_CI)))) {
            addAttributes("RunInCI", "true");
            addAttributes("PipelineExecutionID", getOverriddenStringValue(Config.BUILD_ID,
                                                                          getOverriddenStringValue(config.getProperty(Config.BUILD_ID), NOT_SET)));
            addAttributes("AgentName", getOverriddenStringValue(Config.AGENT_NAME,
                                                                getOverriddenStringValue(config.getProperty(Config.AGENT_NAME), NOT_SET)));
        } else {
            addAttributes("RunInCI", "false");
        }
    }

    private static String getBranchNameUsingGitCommand() {
        //TODO: This currently works only for Mac/Linux. Need to update to make it work for Windows as well
        String[] getBranchNameCommand = new String[]{"git", "rev-parse", "--abbrev-ref", "HEAD"};
        CommandLineResponse response = CommandLineExecutor.execCommand(getBranchNameCommand);
        String branchName = response.getStdOut();
        LOGGER.info(String.format("\tBranch name from git command: '%s': '%s'",
                                  Arrays.toString(getBranchNameCommand), branchName));
        return branchName;
    }

    private static String getBrowserVersion(String browserPath) {
        //TODO: This currently works only for Mac/Linux. Need to update to make it work for Windows as well
        String[] getBrowserVersionCommand = new String[]{browserPath, "-v", "|", "awk", "'{print $2}'"};
        CommandLineResponse response = CommandLineExecutor.execCommand(getBrowserVersionCommand);
        String BrowserVersion = response.getStdOut();
        LOGGER.info(String.format("\tBrowser Version from CLI command: '%s': '%s'",
                                  Arrays.toString(getBrowserVersionCommand), BrowserVersion));
        return BrowserVersion;
    }

    private static boolean isPlatformWeb() {
        return getPlatform().equalsIgnoreCase("Web");
    }

    private static void setRPAttributesFromSystemVariables() {
        setRPAttributesFromEnvVariables();
        setRPAttributesFromSystemProperties();
    }

    private static void setRPAttributesFromSystemProperties() {
        Properties properties = System.getProperties();
        Set<String> set = properties.stringPropertyNames();
        for (String propkey : set) {
            if (propkey.startsWith(RP_PREFIX)) {
                addAttributes(getKeyWithoutPrefix(propkey), properties.getProperty(propkey));
            }
        }
    }

    private static void setRPAttributesFromEnvVariables() {
        Map<String, String> map = System.getenv();
        for (String envKey : map.keySet()) {
            if (envKey.startsWith(RP_PREFIX)) {
                addAttributes(getKeyWithoutPrefix(envKey), map.get(envKey));
            }
        }
    }

    private static String getKeyWithoutPrefix(String key) {
        return key.substring(RP_PREFIX.length());
    }

    private static void addAttributes(String key, String value) {
        if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
            System.out.println("Adding attribute: key: " + key + ", with value: " + value);
            itemAttributesRQSet.add(new ItemAttributesRQ(key, value));
        }
    }
}
