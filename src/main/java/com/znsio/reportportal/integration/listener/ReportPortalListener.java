package com.znsio.reportportal.integration.listener;

import com.epam.reportportal.testng.BaseTestNGListener;
import com.znsio.reportportal.integration.properties.ReportPortalPropertiesOverloader;
import com.znsio.reportportal.integration.utils.ParamOverrideTestNgService;

public class ReportPortalListener extends BaseTestNGListener {
    public ReportPortalListener() {
        super(new ParamOverrideTestNgService(ReportPortalPropertiesOverloader.getProperties()));
    }
}
