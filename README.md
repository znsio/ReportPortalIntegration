# Introduction

1. Utility to easily integrate ReportPortal with your JAVA-TestNG automation framework
2. This is a library which implements basic utilities
   for [ReportPortal](https://reportportal.io/docs/) `(com.epam.reportportal)`

# Utilities

1. `ReportPortalLogger`: For logging messages to ReportPortal at different levels like DEBUG, INFO WARN
2. `ScreenShotManager`: Takes care of following
    1. Capturing Screenshot using selenium
    2. Descaling/Reducing file size of the taken screenshot
    3. Saving the screenshots in managed test level hierarchies
3. `CommandLineExecutor` and `CommandLineResponse`: Take care of running any CLI commands and storing its Standard
   Output, Error responses

# Build

`mvn clean install -DskipTests`
> If facing issues with dependencies not being resolved from https://jitpack.io, then check the `settings.xml` file
> you're using for building your maven projects. If you've proxies configured in the same, then make sure `jitpack.io`
> is part of `nonProxyHosts` configuration. For instance
```xml

<proxy>
    <id>httpmyproxy</id>
    <active>true</active>
    <protocol>http</protocol>
    <host>someHost</host>
    <port>8080</port>
    <username>UserName</username>
    <password>Password</password>
    <nonProxyHosts>*.google.com|*jitpack.io</nonProxyHosts>
</proxy>
```

# How to consume the dependency

   ```
   <dependency>
      <groupId>com.github.znsio</groupId>
      <artifactId>ReportPortalIntegration</artifactId>
      <version>x.x.x</version>
   </dependency>
   ```

# How to configure ReportPortal in your automation framework

1. **Listener Configuration**
    1. **Approach-1 Listener in TestNG XML file:** Add the following line to configure ReportPortal listener in the xml
       file where you have configured your testng suite
        1. **Without overloading properties**
           ```
           <listener class-name="com.epam.reportportal.testng.ReportPortalTestNGListener"/>
           ```
        2. **With overloading properties**
           ```
           <listener class-name="com.znsio.rpi.listener.ReportPortalListener"/>
           ```
    2. **Approach-2 Listener in Maven Surefire Plugin:** Add the following snippet to configure ReportPortal listener in
       the `pom.xml` file where you have configured your `maven-surefire-plugin`
        1. **Without overloading properties**
           ```
           <property>
              <name>listener</name>
              <value>com.epam.reportportal.testng.ReportPortalTestNGListener</value>
           </property>
           ```
        2. **With overloading properties**
           ```
           <property>
              <name>listener</name>
              <value>com.znsio.rpi.listener.ReportPortalListener</value>
           </property>
           ```
    2. **Approach-3:** Use the following snippet to configure ReportPortal listener when creating TestNG file
       programmatically
        1. **Without overloading properties**
           ```
           ReportPortalTestNGListener reportPortalTestNGListener = new ReportPortalTestNGListener();
           testNG.addListener(reportPortalTestNGListener);
           ```
        2. **With overloading properties**
           ```
           ReportPortalListener reportPortalListener = new ReportPortalListener();
           testNG.addListener(reportPortalListener);
           ```
    3. For more details on listener configuration,
       refer [reportportal-agent-java-testNG](https://github.com/reportportal/agent-java-testNG)
2. **Properties file:** Create a `reportportal.properties` file either in `src/main/resources/` or
   in `src/test/resources` directory in your automation framework with the following mandatory attributes
   ```
   rp.endpoint = <Your Endpoint>
   rp.uuid = <Your UDID>
   rp.launch = <Your Launch Name>
   rp.project = <Your Project Name>
   ```
3. **ReportPortal Attributes and Properties Overlaoding:** Use the `ReportPortalPropertiesOverloader` class defined
   under `src/main/java/com/znsio/rpi/properties` path to change any attributes/property value for ReportPortal at
   runtime
4. **Launch attributes related to CI (Pipeline) execution**: The `setPipelineAttributes` method
   of `ReportPortalPropertiesOverloader` class takes care of setting the following attributes in case the test execution
   is happening on CI (Pipeline)
   ```
   BUILD_ID
   AGENT_NAME
   BRANCH_NAME
   ```
   All these attributes will be set as launch attributes if the execution is happening on CI, and they are either set at
   System property or System environment variable. If your pipeline is using different keys to set the above attributes
   then the ones we're using above, for each such key, you can define the new key value in the `config.proprties` file
   of your automation framework like below:
   ```
   BUILD_ID=BUILD_BUILDID
   BRANCH_NAME=BUILD_SOURCEBRANCHNAME
   ```
5. For more details on configuration and additional parameters for `reportportal.properties`,
   refer [reportportal-client-java](https://github.com/reportportal/client-java)
   and [reportportal-agent-java-testNG](https://github.com/reportportal/agent-java-testNG)

# How to use the ReportPortal utilities

1. `ReportPortalPropertiesOverloader` class takes care of dynamically setting Report Portal properties at run time like
   launch name, description, attributes etc
    1. Use `parameters.setLaunchName(<LaunchName>)` to set launch name
    2. Use `parameters.setDescription(<LaunchDescription>)` to set launch description
    3. Use `addAttributes(<Key>, <Value>)` to set any launch attribute. For instance,
       `addAttributes("OS", System.getProperty("os.name"));`
    4. **Dynamic properties:** For setting any additional property to launch attributes which is not already configured
       in `ReportPortalPropertiesOverloader` class, set that attribute key and value at either System Property level or
       at Environment variable. Any key with prefix `RP_` set at System property or Environment variable level will be
       set as ReportPortal's launch attribute. The method which takes care of this configuration
       is `setRPAttributesFromSystemVariables` defined inside `ReportPortalPropertiesOverloader` class.
       For instance, if you're setting environment variable like `export RP_Version=0.0.1`, then on ReportPortal, you'll
       see the corresponding launch attribute as `Version:0.0.1`
2. `ReportPortalLogger` class has all its public methods defined as static, so it doesn't require the consumer to create
   an object of this class. Following are the methods we can use to report logs to ReportPortal
    1. `ReportPortalLogger.logInfoMessage(String message)`: Logs `message` at INFO level to `ReportPortal` as well as
       console
    2. `ReportPortalLogger.logDebugMessage(String message)`: Logs `message` at DEBUG level to `ReportPortal`
    3. `ReportPortalLogger.logWarningMessage(String message)`: Logs `message` at WARN level to `ReportPortal`
    4. `ReportPortalLogger.captureAndAttachScreenshot(WebDriver webDriver)`: This method implicitly
       calls `captureScreenShot` method of `ScreenShotManager` class to capture the screenshot and then
       calls `attachFileInReportPortal` method of `ReportPortalLogger` class to log the screenshot to ReportPortal
    5. `ReportPortalLogger.captureAndAttachScreenshot(WebDriver webDriver, Sting message)`: It is overloading
       the `captureScreenShot` method with additional capability of logging the message (passed in parameter) to
       ReportPortal along with the screenshot
    6. `ReportPortalLogger.attachFileInReportPortal(String message, File file)`: Logs `message` and attaches `file` at
       INFO level to `ReportPortal`
2. `ScreenShotManager` class has all its public methods defined as static, so it doesn't require the consumer to create
   an object of this class. Following are the methods we can use to capture screenshots. This class creates the
   following directory under your project directory "TestReport/Screenshots" where the screenshots will be saved
    1. `ScreenShotManager.captureScreenShot(WebDriver webDriver)`: This method will take the calling step as its
       fileName, and its parent as the testName. It will then call `processScreenShot()` method to capture, descale and
       save the screenshot. The method returns a File object where the captured file is stored.
    2. `ScreenShotManager.processScreenShot(WebDriver driver, String testName, String fileName)`: This method performs
       the following operations:
        1. Captures the screenshot using Selenium `TakesScreenshot` utility
        2. Reduce the screenshot file size by converting the png file to jpg file
        3. Assign the screenshot file name with this format "<CurrentTimeStamp>_<fileName>.jpg"
        4. Creates a `testName` directory under the "TestReport/Screenshots" and will save the screenshot here. If
           the `testName` value is null or empty, the screenshot will be saved directly under "TestReport/Screenshots"
           directory
3. `CommandLineExecutor` class has all its public methods defined as static, so it doesn't require the consumer to
   create an object of this class. Following are the methods we can use to run CLI commands using Java
    1. `CommandLineExecutor.execCommand(final String[] command)`: Run the command passed in the parameter value and
       returns the response of type `CommandLineResponse`. The method automatically takes care of identifying the
       platform (Mac, Windows etc)
    2. `CommandLineExecutor.execCommand(final String[] command, int timeoutInSeconds)`: It is overloading
       the `execCommand` method with additional capability of specifying the timeout in seconds. The process will wait
       for the specified time until we save the response from the command execution
4. `CommandLineResponse` class stores the CLI command execution response as part of following methods:
    1. `setExitCode(int exitCode)` and `getExitCode()`: Getters and Setters for `process.exitValue()`
    2. `setStdOut(String stdOut)` and `getStdOut()`: Getters and Setters for `process.getInputStream()`
    3. `setErrOut(String errOut)` and `getErrOut()`: Getters and Setters for `process.getErrorStream()`
5. `@Step` annotation: Modularise your tests by dividing into multiple Steps to achieve Nested Steps in ReportPortal.
   Nested steps is a common way to group your test logs into small described pieces. For more details and how to use
   nested steps, refer [ReportPortal-NestedSteps](https://github.com/reportportal/client-java/wiki/Nested-steps)
