package greendream.cultivatech.platform.shared.infrastructure.configuration.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI cultivatechOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cultivatech API")
                        .version("1.0.0")
                        .description("REST API documentation for Cultivatech platform.")
                        .contact(new Contact().name("Cultivatech Platform Team"))
                        .license(new License().name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token issued by POST /api/v1/authentication/sign-in")))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }

    @Bean
    public OpenApiCustomizer cultivatechPublicEndpointsSecurityCustomizer() {
        return openApi -> {
            excludeSecurity(openApi, "/api/v1/authentication/sign-in", "post");
            excludeSecurity(openApi, "/api/v1/authentication/sign-up", "post");
            excludeSecurity(openApi, "/api/v1/roles", "get");
            excludeSecurity(openApi, "/api/v1/roles/{roleId}", "get");
        };
    }

    private void excludeSecurity(OpenAPI openApi, String path, String method) {
        if (openApi.getPaths() == null) {
            return;
        }
        PathItem pathItem = openApi.getPaths().get(path);
        if (pathItem == null) {
            return;
        }
        Operation operation = switch (method) {
            case "get" -> pathItem.getGet();
            case "post" -> pathItem.getPost();
            case "put" -> pathItem.getPut();
            case "patch" -> pathItem.getPatch();
            case "delete" -> pathItem.getDelete();
            default -> null;
        };
        if (operation != null) {
            operation.setSecurity(new ArrayList<>());
        }
    }
}
