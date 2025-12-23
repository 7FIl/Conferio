package com.conference.management_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Development Server"))
                .addServersItem(new Server()
                        .url("http://api.conference.local/api")
                        .description("Production Server"))
                .info(new Info()
                        .title("Conference Management API")
                        .version("1.0.0")
                        .summary("Professional REST API for Conference Management")
                        .description("A comprehensive REST API for managing conference sessions, proposals, feedback, and user registrations. " +
                                "Fully documented endpoints with JWT-based authentication and interactive testing interface.")
                        .termsOfService("https://api.conference.local/terms")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@conference.local")
                                .url("https://conference.local"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Bearer Token Authentication. Obtain token from /api/auth/login or /api/auth/register.")));
    }
}
