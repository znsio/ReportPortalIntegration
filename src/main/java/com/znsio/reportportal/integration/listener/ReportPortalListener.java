package com.znsio.reportportal.integration.listener;

import com.epam.reportportal.testng.BaseTestNGListener;
import com.znsio.reportportal.integration.properties.ReportPortalPropertiesOverloader;
import com.znsio.reportportal.integration.utils.ParamOverrideTestNgService;

public class ReportPortalListener extends BaseTestNGListener {
    private static final ParamOverrideTestNgService paramOverrideTestNgService = new ParamOverrideTestNgService(ReportPortalPropertiesOverloader.getProperties());
    private static boolean isInitialized = false;

    public ReportPortalListener() {
        super(paramOverrideTestNgService);
    }

    @Override
    public void onExecutionStart() {
        if (isInitialized) {
            System.out.println("onExecutionStart: ReportPortalListener already initialized");
        } else {
            super.onExecutionStart();
            isInitialized = true;
        }
    }

    @Override
    public void onExecutionFinish() {
        isInitialized = false;
        super.onExecutionFinish();
    }
}
