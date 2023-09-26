package com.znsio.reportportal.integration.listener;

import com.znsio.reportportal.integration.utils.ParamOverrideTestNgService;
import com.epam.reportportal.testng.BaseTestNGListener;
import com.znsio.reportportal.integration.properties.ReportPortalPropertiesOverloader;

public class ReportPortalListener extends BaseTestNGListener {
    public ReportPortalListener() {
        super(new ParamOverrideTestNgService(ReportPortalPropertiesOverloader.getProperties()));
    }
}
