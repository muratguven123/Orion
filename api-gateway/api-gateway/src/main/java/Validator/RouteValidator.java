package Validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints=List.of(
            "/api/auth/register",
            "/api/auth/login/email",
            "/api/auth/login/sms/send-otp",
            "/api/auth/login/sms/verify",
            "/api/auth/refresh-token",
            "/euraka/**",
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs",
            "/v3/api-docs/swagger-config",
            "/swagger-resources",
            "/webjars/",
            "/webjars/swagger-ui",
            "/account-service/v3/api-docs",
            "/auth-service/v3/api-docs",
            "/invest-service/v3/api-docs"
    );
    public Predicate<ServerHttpRequest> isSecured=
            request ->openApiEndpoints
                    .stream()
                    .noneMatch(uri->request.getURI().getPath().contains(uri));
}
