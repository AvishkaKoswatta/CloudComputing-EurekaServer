package com.example.eureka_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


/**
 * Security configuration for Eureka Server
 *
 * This configuration:
 * - Allows access to Eureka endpoints without authentication
 * - Secures the dashboard with basic authentication
 * - Disables CSRF for service registration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for Eureka endpoints (required for service registration)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure authorization
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/eureka/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/", "/actuator/**").authenticated()
                        .anyRequest().permitAll()
                )

                // Enable HTTP Basic authentication
                .httpBasic(httpBasic -> httpBasic
                        .realmName("Eureka Server")
                )

                // Secure headers configuration (updated syntax)
                .headers(headers -> headers
                        .contentTypeOptions(contentType -> {})
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                        )
                        .frameOptions(frameOptions -> frameOptions
                                .deny()
                        )
                );

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername("admin")
                .password("{noop}admin123")  // {noop} means no encoding
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
