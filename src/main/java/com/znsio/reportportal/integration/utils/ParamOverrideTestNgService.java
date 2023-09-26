package com.znsio.reportportal.integration.utils;

import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.service.Launch;
import com.epam.reportportal.service.ReportPortal;
import com.epam.reportportal.testng.TestNGService;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import rp.com.google.common.base.Strings;
import rp.com.google.common.base.Supplier;

import java.util.Calendar;

public class ParamOverrideTestNgService extends TestNGService {
    public ParamOverrideTestNgService(ListenerParameters parameters) {
        super(getLaunchOverriddenProperties(parameters));
    }

    private static Supplier<Launch> getLaunchOverriddenProperties(ListenerParameters parameters) {
        ReportPortal reportPortal = ReportPortal.builder().withParameters(parameters).build();
        StartLaunchRQ rq = buildStartLaunch(reportPortal.getParameters());
        return new Supplier<Launch>() {
            @Override
            public Launch get() {
                return reportPortal.newLaunch(rq);
            }
        };
    }

    private static StartLaunchRQ buildStartLaunch(ListenerParameters parameters) {
        StartLaunchRQ rq = new StartLaunchRQ();
        rq.setName(parameters.getLaunchName());
        rq.setStartTime(Calendar.getInstance().getTime());
        rq.setAttributes(parameters.getAttributes());
        rq.setMode(parameters.getLaunchRunningMode());
        if (!Strings.isNullOrEmpty(parameters.getDescription())) {
            rq.setDescription(parameters.getDescription());
        }
        return rq;
    }
}
