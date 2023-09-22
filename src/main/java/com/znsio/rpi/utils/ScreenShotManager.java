package com.znsio.rpi.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenShotManager {
    private static final String directoryPath = System.getProperty("user.dir") + File.separator +
            "TestReport/Screenshots";
    private static final Logger LOGGER = Logger.getLogger(ScreenShotManager.class.getSimpleName());

    public static File captureScreenShot(WebDriver webDriver) {
        String testName = Thread.currentThread().getStackTrace()[4].getMethodName();
        String fileName = Thread.currentThread().getStackTrace()[3].getMethodName();
        return processScreenShot(webDriver, testName, fileName);
    }

    private static File processScreenShot(WebDriver driver, String testName, String fileName) {
        fileName = normaliseScenarioName(getPrefix() + "-" + fileName);
        File destinationFile;
        if (testName != null && !testName.isEmpty()) {
            destinationFile = createScreenshotFile(directoryPath + File.separator + testName, fileName);
        } else {
            destinationFile = createScreenshotFile(directoryPath, fileName);
        }
        destinationFile.getParentFile().mkdirs();
        LOGGER.info("The screenshot will be placed here : " + destinationFile.getAbsolutePath());
        try {
            File screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            LOGGER.info("Original screenshot : " + screenShot.getAbsolutePath());
            reduceFileSize(screenShot, destinationFile);
            LOGGER.info("The screenshot is available here : " + destinationFile.getAbsolutePath());
        } catch (RuntimeException e) {
            LOGGER.info(
                    "ERROR: Unable to create screenshot: '" + destinationFile.getAbsolutePath() + "' \n");
            LOGGER.debug(ExceptionUtils.getStackTrace(e));
        }
        return destinationFile;
    }

    private static String normaliseScenarioName(String scenarioName) {
        return scenarioName.replaceAll("[`~ !@#$%^&*()\\-=+\\[\\]{}\\\\|;:'\",<.>/?]", "_")
                .replaceAll("__", "_").replaceAll("__", "_");
    }

    private static String getPrefix() {
        return java.time.LocalDateTime.now().toString();
    }


    private static File createScreenshotFile(String dirName, String fileName) {
        fileName = fileName.endsWith(".jpg") ? fileName : fileName + ".jpg";
        return new File(dirName + File.separator + fileName);
    }

    private static void reduceFileSize(File source, File destination) {
        try {
            BufferedImage bufferedImage = ImageIO.read(source);
            // Create a blank, RGB, same width and height, and a white background
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            // Write to a JPEG file
            ImageIO.write(newBufferedImage, "jpg", destination);
        } catch (IOException e) {
            LOGGER.info(
                    "ERROR: Unable to read from source file: '" + source.getAbsolutePath()
                            + "' or write to destination file: '" + destination.getAbsolutePath() + "'\n");
            LOGGER.debug(ExceptionUtils.getStackTrace(e));
        }
    }
}
