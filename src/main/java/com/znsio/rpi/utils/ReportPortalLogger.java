package com.znsio.rpi.utils;

import com.epam.reportportal.service.ReportPortal;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Date;

public class ReportPortalLogger {
    private static final Logger LOGGER = Logger.getLogger(ReportPortalLogger.class.getSimpleName());
    public static final String DEBUG = "DEBUG";
    public static final String INFO = "INFO";
    public static final String WARN = "WARN";

    private ReportPortalLogger() {
        LOGGER.debug("ReportPortalLogger - private constructor");
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
}
