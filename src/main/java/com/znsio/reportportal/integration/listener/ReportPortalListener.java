package com.znsio.reportportal.integration.listener;

import com.epam.reportportal.testng.BaseTestNGListener;
import com.znsio.reportportal.integration.properties.ReportPortalPropertiesOverloader;
import com.znsio.reportportal.integration.utils.ParamOverrideTestNgService;

public class ReportPortalListener extends BaseTestNGListener {
    // Volatile variable to ensure visibility of changes across threads
    private static volatile ReportPortalListener instance;

    private ReportPortalListener() {
        super(new ParamOverrideTestNgService(ReportPortalPropertiesOverloader.getProperties()));
    }

    public static ReportPortalListener getInstance() {
        if (instance == null) {
            synchronized (ReportPortalListener.class) {
                if (instance == null) {
                    System.out.println("Initializing ReportPortalListener");
                    instance = new ReportPortalListener();
                }
            }
        } else {
            System.out.println("ReportPortalListener already initialized");
        }
        return instance;
    }
}
