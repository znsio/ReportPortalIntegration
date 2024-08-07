package com.znsio.reportportal.integration.utils;

import com.epam.reportportal.service.ReportPortal;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.Date;

import static com.znsio.reportportal.integration.utils.ScreenShotManager.captureScreenShot;

public class ReportPortalLogger {
    private static final Logger LOGGER = Logger.getLogger(ReportPortalLogger.class.getSimpleName());
    public static final String DEBUG = "DEBUG";
    public static final String INFO = "INFO";
    public static final String ERROR = "ERROR";
    public static final String WARN = "WARN";
    private static final String DEFAULT_MESSAGE_FOR_SCREENSHOT = "Captured Screenshot";

    private ReportPortalLogger() {
        LOGGER.debug("ReportPortalLogger - private constructor");
    }

    public static void captureAndAttachScreenshot(WebDriver webDriver) {
        File destinationFile = captureScreenShot(webDriver);
        attachScreenshotInReportPortal(DEFAULT_MESSAGE_FOR_SCREENSHOT, destinationFile);
    }

    public static void captureAndAttachScreenshot(WebDriver webDriver, String message) {
        File destinationFile = captureScreenShot(webDriver);
        attachScreenshotInReportPortal(message, destinationFile);
    }

    public static void attachFileInReportPortal(String message, File destinationFile) {
        boolean isEmitLogSuccessful = ReportPortal.emitLog(message, INFO, new Date(), destinationFile);
        if (!isEmitLogSuccessful) {
            LOGGER.error(String.format("'%s' - Upload of file: '%s'::'%s' to ReportPortal failed",
                    getCallingClassAndMethodName(), message, destinationFile));
        }
    }

    public static void logDebugMessage(String message) {
        LOGGER.debug(message);
        logMessage(message, DEBUG);
    }

    public static void logWarningMessage(String message) {
        LOGGER.warn(message);
        logMessage(message, WARN);
    }

    public static void logInfoMessage(String message) {
        LOGGER.info(message);
        logMessage(message, INFO);
    }

    public static void logErrorMessage(String message) {
        LOGGER.error(message);
        logMessage(message, ERROR);
    }

    private static String getCallingClassAndMethodName() {
        try {
            final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            return ste[3].getClassName() + ":" + ste[3].getMethodName();
        } catch (ArrayIndexOutOfBoundsException e) {
            LOGGER.error("Unable to get calling method class/method name");
            return "ReportPortal";
        }
    }

    private static void logMessage(String message, String level) {
        boolean isEmitLogSuccessful = ReportPortal.emitLog(message, level, new Date());
        if (!isEmitLogSuccessful) {
            LOGGER.error(String.format("'%s' - Logging message: '%s' to ReportPortal failed",
                    getCallingClassAndMethodName(), message));

        }
    }

    private static void attachScreenshotInReportPortal(String logMessage, File destinationFile) {
        try {
            attachFileInReportPortal(logMessage, destinationFile);
        } catch (RuntimeException e) {
            LOGGER.info(
                    "ERROR: Unable to upload screenshot: '" + destinationFile.getAbsolutePath() +
                            "' to ReportPortal\n");
            LOGGER.debug(ExceptionUtils.getStackTrace(e));
        }
    }
}
