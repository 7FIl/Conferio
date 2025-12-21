package com.conference.management_system.security;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Rate limiting interceptor to prevent brute force attacks on login endpoint.
 * Allows 5 login attempts per minute per IP address.
 */
@Component
@Slf4j
public class RateLimitingInterceptor implements HandlerInterceptor {
    
    // Store buckets per IP address
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    
    // 5 attempts per minute (300 seconds)
    private final Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Only rate limit login attempts
        if (request.getRequestURI().contains("/api/auth/login")) {
            String clientIp = getClientIp(request);
            
            Bucket bucket = cache.computeIfAbsent(clientIp, k -> Bucket4j.builder()
                    .addLimit(limit)
                    .build());
            
            // Try to consume a token
            if (bucket.tryConsume(1)) {
                log.debug("Login attempt allowed from IP: {}", clientIp);
                return true;
            } else {
                // Rate limit exceeded - calculate retry-after
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                
                // Estimate time to wait (approximately 60 seconds for next available slot)
                long retryAfterSeconds = 60;
                response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(retryAfterSeconds));
                response.getWriter().write("{\"error\":\"Too many login attempts. Please try again in " + 
                        retryAfterSeconds + " seconds.\"}");
                log.warn("Rate limit exceeded for IP: {} - Too many login attempts", clientIp);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Extract client IP address from request.
     * Handles X-Forwarded-For header for proxied requests.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP if multiple are present
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}

