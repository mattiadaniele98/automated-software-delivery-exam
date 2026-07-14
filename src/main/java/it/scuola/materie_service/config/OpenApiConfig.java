package it.scuola.materie_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione di Swagger/OpenAPI: genera la documentazione delle API
 * leggendo le annotazioni sui controller e aggiunge il supporto per
 * l'autenticazione JWT (pulsante "Authorize" in Swagger UI).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configOpenApi(
            @Value("${spring.application.name}") String name,
            @Value("${app.version}") String version,
            @Value("${app.description}") String description) {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title(name)
                        .version(version)
                        .description(description)
                        .license(new License()
                                .name("Apache License, Version 2.0")
                                .identifier("Apache-2.0")
                                .url("https://opensource.org/license/apache-2-0/")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Inserisci il token JWT ottenuto da utenti-service")));
    }
}
