package com.example.eureka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Discovery Server for Car Rental System Microservices
 *
 * This server provides:
 * - Service registration and discovery
 * - Health monitoring
 * - Load balancing support
 * - Web dashboard for service monitoring
 *
 * Access the dashboard at: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
		System.out.println("=================================");
		System.out.println("🚗 Car Rental System - Eureka Server Started!");
		System.out.println("📊 Dashboard: http://localhost:8761");
		System.out.println("=================================");
	}
}