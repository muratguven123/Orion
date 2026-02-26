package org.murat.orion.api_gateway.config;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    /**
     * Gateway route tanımlarından *-api-docs pattern'ine uyan route'ları bulur
     * ve Swagger UI URL listesini otomatik oluşturur.
     * Yeni servis eklendiğinde sadece gateway route'u eklenmesi yeterlidir.
     */
    @Bean
    public CommandLineRunner configureSwaggerUrls(
            RouteDefinitionLocator routeDefinitionLocator,
            SwaggerUiConfigProperties swaggerUiConfigProperties) {
        return args -> {
            List<RouteDefinition> routes = routeDefinitionLocator.getRouteDefinitions()
                    .collectList()
                    .block(Duration.ofSeconds(10));

            Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new LinkedHashSet<>();

            if (routes != null) {
                routes.stream()
                    .filter(route -> route.getId().endsWith("-api-docs"))
                    .sorted(Comparator.comparing(RouteDefinition::getId))
                    .forEach(route -> {
                        String serviceName = route.getId().replace("-api-docs", "");
                        AbstractSwaggerUiConfigProperties.SwaggerUrl url =
                            new AbstractSwaggerUiConfigProperties.SwaggerUrl();
                        url.setName(formatServiceName(serviceName));
                        url.setUrl("/" + serviceName + "/v3/api-docs");
                        urls.add(url);
                    });
            }

            swaggerUiConfigProperties.setUrls(urls);
            swaggerUiConfigProperties.setPath("/swagger-ui.html");
            swaggerUiConfigProperties.setDisableSwaggerDefaultUrl(true);
        };
    }

    private String formatServiceName(String name) {
        String[] parts = name.split("-");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }
}
