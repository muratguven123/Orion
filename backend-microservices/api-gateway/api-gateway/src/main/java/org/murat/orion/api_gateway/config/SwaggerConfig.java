package org.murat.orion.api_gateway.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SwaggerConfig {

    @Bean
    @Primary
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();

        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

        AbstractSwaggerUiConfigProperties.SwaggerUrl authUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
        authUrl.setName("Auth Service");
        authUrl.setUrl("/auth-service/v3/api-docs");
        urls.add(authUrl);

        AbstractSwaggerUiConfigProperties.SwaggerUrl accountUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
        accountUrl.setName("Account Service");
        accountUrl.setUrl("/account-service/v3/api-docs");
        urls.add(accountUrl);

        AbstractSwaggerUiConfigProperties.SwaggerUrl investUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl();
        investUrl.setName("Invest Service");
        investUrl.setUrl("/invest-service/v3/api-docs");
        urls.add(investUrl);

        properties.setUrls(urls);
        properties.setPath("/swagger-ui.html");
        properties.setDisableSwaggerDefaultUrl(true);

        return properties;
    }
}
