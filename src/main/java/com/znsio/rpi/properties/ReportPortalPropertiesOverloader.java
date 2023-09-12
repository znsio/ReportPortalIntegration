package com.znsio.rpi.properties;

import com.znsio.rpi.utils.commandline.CommandLineExecutor;
import com.znsio.rpi.utils.commandline.CommandLineResponse;
import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.utils.properties.PropertiesLoader;
import com.epam.ta.reportportal.ws.model.attribute.ItemAttributesRQ;
import org.apache.log4j.Logger;
import org.testng.util.Strings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ReportPortalPropertiesOverloader {
    private static ListenerParameters parameters = new ListenerParameters(PropertiesLoader.load());
    private static Set<ItemAttributesRQ> itemAttributesRQSet = new HashSet<>();
    private static final Logger LOGGER = Logger.getLogger(ReportPortalPropertiesOverloader.class.getName());
    private static final String WEB_AUTOMATION = "WebAutomation";
    private static final String APP_AUTOMATION = "AppAutomation";
    private static final Properties rpProperties = Config.loadProperties(System.getProperty("CONFIG"));

    public static ListenerParameters getProperties() {
        setLaunchName();
        setSystemAttributes();
        setTestAttributes();
        setPipelineAttributes();
        parameters.setAttributes(itemAttributesRQSet);
        return parameters;
    }

    private static void setLaunchName() {
        if (isPlatformWeb()) {
            parameters.setLaunchName(rpProperties.getProperty(Config.APP_NAME) + " - " + WEB_AUTOMATION + " - " +
                    rpProperties.getProperty(Config.PLATFORM).toUpperCase());
        } else {
            parameters.setLaunchName(rpProperties.getProperty(Config.APP_NAME) + " - " + APP_AUTOMATION + " - " +
                    rpProperties.getProperty(Config.PLATFORM).toUpperCase());
        }
    }

    private static void setSystemAttributes() {
        addAttributes("OS", System.getProperty("os.name"));
        addAttributes("Username", System.getProperty("user.name"));
        try {
            if (Strings.isNotNullAndNotEmpty(InetAddress.getLocalHost().getHostName())) {
                String hostName = InetAddress.getLocalHost().getHostName();
                addAttributes("HostName", hostName);
            }
        } catch (UnknownHostException ex) {
            LOGGER.warn("Unable to set Report Portal " +
                    "Attributes for HostName: " + ex);
        }
    }

    private static void setTestAttributes() {
        addAttributes("TargetEnvironment", rpProperties.getProperty(Config.TARGET_ENVIRONMENT));
        addAttributes("Platform", rpProperties.getProperty(Config.PLATFORM).toUpperCase());
        if (isPlatformWeb()) {
            addAttributes("Browser", rpProperties.getProperty(Config.BROWSER));
        } else {
            addAttributes("App", rpProperties.getProperty(Config.APP_PACKAGE_NAME));
            addAttributes("MobilabEnabled", rpProperties.getProperty(Config.IS_MOBILAB));
        }
        addAttributes("VisualEnabled", rpProperties.getProperty(Config.IS_VISUAL));
    }

    private static void setPipelineAttributes() {
        if (Strings.isNotNullAndNotEmpty(System.getenv("BUILD_BUILDID"))) {
            addAttributes("RunInCI", "true");
            addAttributes("PipelineExecutionID", System.getenv("BUILD_BUILDID"));
            addAttributes("AgentName", System.getenv("AGENT_NAME"));
            addAttributes("AutomationBranch", System.getenv("BUILD_SOURCEBRANCHNAME"));
        } else {
            addAttributes("RunInCI", "false");
            addAttributes("AutomationBranch", getBranchNameUsingGitCommand());
        }
    }

    private static String getBranchNameUsingGitCommand() {
        String[] getBranchNameCommand = new String[]{"git", "rev-parse", "--abbrev-ref", "HEAD"};
        CommandLineResponse response = CommandLineExecutor.execCommand(getBranchNameCommand);
        String branchName = response.getStdOut();
        LOGGER.info(String.format("\tBranch name from git command: '%s': '%s'",
                Arrays.toString(getBranchNameCommand), branchName));
        return branchName;
    }

    private static String getBrowserVersion(String browserPath) {
        String[] getBrowserVersionCommand = new String[]{browserPath, "-v", "|", "awk", "'{print $2}'"};
        CommandLineResponse response = CommandLineExecutor.execCommand(getBrowserVersionCommand);
        String BrowserVersion = response.getStdOut();
        LOGGER.info(String.format("\tBrowser Version from CLI command: '%s': '%s'",
                Arrays.toString(getBrowserVersionCommand), BrowserVersion));
        return BrowserVersion;
    }

    private static boolean isPlatformWeb() {
        return rpProperties.getProperty(Config.PLATFORM).equalsIgnoreCase("Web");
    }

    private static void addAttributes(String key, String value) {
        if (Strings.isNotNullAndNotEmpty(key) && Strings.isNotNullAndNotEmpty(value)) {
            itemAttributesRQSet.add(new ItemAttributesRQ(key, value));
        }
    }
}
