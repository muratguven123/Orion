package com.murat.orion.auth_service.AuthDomain.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8222").description("API Gateway"),
                        new Server().url("http://localhost:9000").description("Auth Service Direct")
                ))
                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0")
                        .description("Orion Banking Application - Auth Service API Documentation")
                        .contact(new Contact()
                                .name("Murat")
                                .email("murat@orion.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token girin. Örnek: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")));
    }
}
