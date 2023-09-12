package com.znsio.rpi.listener;

import com.znsio.rpi.utils.ParamOverrideTestNgService;
import com.epam.reportportal.testng.BaseTestNGListener;
import com.znsio.rpi.properties.ReportPortalPropertiesOverloader;

public class ReportPortalListener extends BaseTestNGListener {
    public ReportPortalListener() {
        super(new ParamOverrideTestNgService(ReportPortalPropertiesOverloader.getProperties()));
    }
}
