package com.example.eureka_server.controller;

import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom dashboard controller for Eureka Server
 * Provides additional monitoring and information endpoints
 */
@Controller
public class DashboardController {

    @GetMapping("/info")
    @ResponseBody
    public Map<String, Object> getServerInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        info.put("server", "Car Rental System - Eureka Server");
        info.put("version", "1.0.0");
        info.put("status", "UP");

        // Get registry information
        EurekaServerContext serverContext = EurekaServerContextHolder.getInstance().getServerContext();
        if (serverContext != null) {
            PeerAwareInstanceRegistry registry = serverContext.getRegistry();
            info.put("registered-services", registry.getApplications().size());
            info.put("total-instances", registry.getApplications().getRegisteredApplications().stream()
                    .mapToInt(app -> app.getInstances().size())
                    .sum());
        }

        return info;
    }

    @GetMapping("/health-check")
    @ResponseBody
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        health.put("uptime", getUptime());

        return health;
    }

    @GetMapping("/services")
    @ResponseBody
    public Map<String, Object> getRegisteredServices() {
        Map<String, Object> services = new HashMap<>();

        EurekaServerContext serverContext = EurekaServerContextHolder.getInstance().getServerContext();
        if (serverContext != null) {
            PeerAwareInstanceRegistry registry = serverContext.getRegistry();

            registry.getApplications().getRegisteredApplications().forEach(app -> {
                Map<String, Object> serviceInfo = new HashMap<>();
                serviceInfo.put("name", app.getName());
                serviceInfo.put("instances", app.getInstances().size());
                serviceInfo.put("status", app.getInstances().isEmpty() ? "DOWN" : "UP");

                app.getInstances().forEach(instance -> {
                    Map<String, Object> instanceInfo = new HashMap<>();
                    instanceInfo.put("instanceId", instance.getInstanceId());
                    instanceInfo.put("host", instance.getHostName());
                    instanceInfo.put("port", instance.getPort());
                    instanceInfo.put("status", instance.getStatus().name());
                    instanceInfo.put("healthCheckUrl", instance.getHealthCheckUrl());
                    instanceInfo.put("lastUpdated", instance.getLastUpdatedTimestamp());
                });

                services.put(app.getName().toLowerCase(), serviceInfo);
            });
        }

        return services;
    }

    private String getUptime() {
        long uptimeMs = System.currentTimeMillis() -
                java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        long uptimeSeconds = uptimeMs / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}