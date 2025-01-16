package com.znsio.reportportal.integration.listener;

import com.znsio.reportportal.integration.utils.ReportPortalLogger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class SkipOnFailureListener implements ITestListener {
    private boolean testFailure = false;
    private String testGroupNameHavingTestFailure = "";
    private String testClassNameHavingTestFailure = "";
    private String testMethodNameHavingTestFailure = "";
    private String currentTestGroupName = "";
    private String currentClassName = "";
    private String currentTestMethodName = "";
    private String messageTemplate =
            "%n\t\tcurrentTestGroupName             : '%s'" +
            "%n\t\tcurrentTestClassName             : '%s'" +
            "%n\t\tcurrentTestMethodName            : '%s'" +
            "%n\t\ttestGroupNameHavingTestFailure   : '%s'" +
            "%n\t\ttestClassNameHavingTestFailure   : '%s'" +
            "%n\t\ttestMethodNameHavingTestFailure  : '%s'" +
            "%n\t\ttestFailure                      : '%s'";

    @Override
    public void onTestStart(ITestResult result) {
        if (result == null) {
            ReportPortalLogger.logErrorMessage("SkipOnFailureListener: onTestStart: result is NULL.");
        }
        currentTestGroupName = result.getTestContext().getCurrentXmlTest().getName();
        currentClassName = result.getMethod().getRealClass().getName();
        currentTestMethodName = result.getMethod().getMethodName();

        if (testFailure && currentTestGroupName.equalsIgnoreCase(testGroupNameHavingTestFailure)) {
            result.setStatus(ITestResult.SKIP);
        } else {
            testFailure = false;
            testGroupNameHavingTestFailure = "";
            testClassNameHavingTestFailure = "";
            testMethodNameHavingTestFailure = "";
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (result == null) {
            ReportPortalLogger.logErrorMessage("SkipOnFailureListener: onTestStart: result is NULL.");
        }
        testFailure = true;
        testGroupNameHavingTestFailure = result.getTestContext().getCurrentXmlTest().getName();
        testClassNameHavingTestFailure = result.getMethod().getRealClass().getSimpleName();
        testMethodNameHavingTestFailure = result.getMethod().getMethodName();
        currentTestGroupName = testGroupNameHavingTestFailure;
        currentClassName = testClassNameHavingTestFailure;
        currentTestMethodName = testMethodNameHavingTestFailure;

        String prefix = "Test has failed";
        String message = String.format("\t" + prefix + messageTemplate,
                                       currentTestGroupName, currentClassName, currentTestMethodName,
                                       testGroupNameHavingTestFailure, testClassNameHavingTestFailure, testMethodNameHavingTestFailure, testFailure);
        ReportPortalLogger.logInfoMessage(message);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (result == null) {
            ReportPortalLogger.logErrorMessage("SkipOnFailureListener: onTestStart: result is NULL.");
        }
        currentTestGroupName = result.getTestContext().getCurrentXmlTest().getName();
        currentClassName = result.getMethod().getRealClass().getName();
        testMethodNameHavingTestFailure = result.getMethod().getMethodName();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result == null) {
            ReportPortalLogger.logErrorMessage("SkipOnFailureListener: onTestStart: result is NULL.");
        }
        currentTestGroupName = result.getTestContext().getCurrentXmlTest().getName();
        currentClassName = result.getMethod().getRealClass().getName();
        currentTestMethodName = result.getMethod().getMethodName();
        if (testFailure && currentTestGroupName.equalsIgnoreCase(testGroupNameHavingTestFailure)) {
            ReportPortalLogger.logErrorMessage("Test SKIPPED because of earlier failure in usecase");
            result.setStatus(ITestResult.SKIP);
        } else {
            testFailure = false;
            testClassNameHavingTestFailure = "";
            testGroupNameHavingTestFailure = "";
            testMethodNameHavingTestFailure = "";
        }
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    @Override
    public void onStart(ITestContext context) {
    }
}
