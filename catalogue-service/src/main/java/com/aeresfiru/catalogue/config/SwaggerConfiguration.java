package com.aeresfiru.catalogue.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Catalogue REST API",
                description = "API documentation for the catalogue service",
                contact = @Contact(
                        name = "Aliaksandr Shtarou",
                        email = "aeresfiru@proton.me"
                )
        )
)
@SecurityScheme(
        name = "keycloak",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${keycloak.uri}/realms/sc/protocol/openid-connect/auth",
                tokenUrl = "${keycloak.uri}/realms/sc/protocol/openid-connect/token",
                scopes = {
                        @OAuthScope(name = "openid"),
                        @OAuthScope(name = "microprofile-jwt"),
                        @OAuthScope(name = "edit_catalogue"),
                        @OAuthScope(name = "view_catalogue")
                }
        ))
)
@Configuration
public class SwaggerConfiguration {
}
