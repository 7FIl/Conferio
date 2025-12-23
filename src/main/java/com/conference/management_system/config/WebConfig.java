package com.conference.management_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.conference.management_system.security.RateLimitingInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * Web configuration to register interceptors for rate limiting and other HTTP-level concerns.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final RateLimitingInterceptor rateLimitingInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor);
    }
}
