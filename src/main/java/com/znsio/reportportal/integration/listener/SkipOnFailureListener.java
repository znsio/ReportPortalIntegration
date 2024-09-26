package com.znsio.reportportal.integration.listener;

import com.znsio.reportportal.integration.utils.ReportPortalLogger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class SkipOnFailureListener implements ITestListener {

    private boolean testFailure = false;
    private String testName;
    private String useCaseName;

    @Override
    public void onTestStart(ITestResult result) {
        String currentUseCaseName = result.getTestContext().getCurrentXmlTest().getName();
        if (testFailure && currentUseCaseName.equalsIgnoreCase(useCaseName)) {
            result.setStatus(ITestResult.SKIP);
        } else {
            testFailure = false;
            testName = "";
            useCaseName = "";
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        testFailure = true;
        useCaseName = result.getTestContext().getCurrentXmlTest().getName();
        testName = result.getMethod().getRealClass().getSimpleName();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Do nothing
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String currentUseCaseName = result.getTestContext().getCurrentXmlTest().getName();
        if (testFailure && currentUseCaseName.equalsIgnoreCase(useCaseName)) {
            ReportPortalLogger.logDebugMessage("Test: '" + result.getMethod().getMethodName() + "' + SKIPPED because of earlier failure in usecase: '" + useCaseName + "':: Test: " + testName);
            result.setStatus(ITestResult.SKIP);
            System.out.println("Test " + currentUseCaseName + " SKIPPED because of earlier test failure");
            ReportPortalLogger.logInfoMessage("Test " + currentUseCaseName + " SKIPPED because of earlier test failure");
            ReportPortalLogger.logErrorMessage("Test SKIPPED because of earlier test failure");
        } else {
            testFailure = false;
            testName = "";
            useCaseName = "";
        }
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    @Override
    public void onStart(ITestContext context) {
    }
}
