package com.cocinadelicia.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${app.openapi.server.dev:http://localhost:8080}")
  private String devServerUrl;

  @Value("${app.openapi.server.prod:https://api.lacocinadelicia.com}")
  private String prodServerUrl;

  @Bean
  public OpenAPI cocinaDeLiciaOpenAPI(
      @Value("${spring.application.name:CocinaDeLicia}") String appName,
      @Value("${app.version:v1.0.0}") String appVersion) {

    final String securitySchemeName = "bearer-jwt";

    return new OpenAPI()
        .info(
            new Info()
                .title(appName + " – API")
                .description(
                    "APIs del backend (Pedidos, Productos, Usuarios, etc.). "
                        + "Documentación generada automáticamente con Springdoc OpenAPI.")
                .version(appVersion)
                .contact(new Contact().name("Cocina DeLicia").email("soporte@lacocinadelicia.com")))
        .servers(
            List.of(
                new Server().url(devServerUrl).description("Desarrollo"),
                new Server().url(prodServerUrl).description("Producción")))
        .externalDocs(
            new ExternalDocumentation()
                .description("Convenciones & Sprints")
                .url("https://tu-docs-o-notion"))
        // ⬇️ Seguridad: esquema Bearer y requirement por defecto
        .components(
            new Components()
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder().group("public").pathsToMatch("/api/**").build();
  }
}
