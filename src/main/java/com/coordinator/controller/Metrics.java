package com.coordinator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coordinator.model.MetricInfo;

import java.util.ArrayList;

@RestController
public class Metrics {

    @Autowired
    private ArrayList<MetricInfo> metricsList;

    @GetMapping(value = "/api/metrics")
    public ArrayList<MetricInfo> getMetricList() {

        try {
            return metricsList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
