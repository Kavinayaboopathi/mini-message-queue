package com.kavinaya.minimessagequeue.controller;

import com.kavinaya.minimessagequeue.dto.MetricsResponse;
import com.kavinaya.minimessagequeue.service.MetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping
    public MetricsResponse getMetrics() {
        return metricsService.getMetrics();
    }
}