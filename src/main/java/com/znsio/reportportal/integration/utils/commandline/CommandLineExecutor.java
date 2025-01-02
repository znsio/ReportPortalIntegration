package com.znsio.reportportal.integration.utils.commandline;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class CommandLineExecutor {
    private static final Logger LOGGER = LogManager.getLogger(CommandLineExecutor.class.getSimpleName());
    private static final int DEFAULT_COMMAND_TIMEOUT = 60;

    public static CommandLineResponse execCommand(final String[] command) {
        return execCommand(command, DEFAULT_COMMAND_TIMEOUT);
    }

    public static CommandLineResponse execCommand(final String[] command, int timeoutInSeconds) {
        String jointCommand = String.join(" ", command);
        String message = "\tExecuting Command: " + jointCommand;
        LOGGER.info(message);
        try {
            CommandLineResponse response = new CommandLineResponse();
            ProcessBuilder builder = new ProcessBuilder(command);
            if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
                builder.command("cmd.exe", "/c", jointCommand);
            } else {
                builder.command("sh", "-c", jointCommand);
            }
            Process process = builder.start();
            process.waitFor(timeoutInSeconds, TimeUnit.SECONDS);
            response.setStdOut(
                    IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8).trim());
            response.setErrOut(
                    IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8).trim());
            LOGGER.info("\t:Exit Code: " + process.exitValue());
            response.setExitCode(process.exitValue());
            LOGGER.info("\t" + response);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error " + message, e);
        }
    }
}
