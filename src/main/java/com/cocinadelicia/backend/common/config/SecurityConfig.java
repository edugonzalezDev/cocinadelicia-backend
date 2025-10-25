package com.cocinadelicia.backend.common.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
@EnableMethodSecurity // habilita @PreAuthorize
class SecurityConfig {

  @Value("${cdd.security.groups-claim:cognito:groups}")
  private String groupsClaim;

  @Value("${cdd.security.required-audience:}")
  private String requiredAudience; // opcional

  @Bean
  SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth
                    // Públicos (swagger/health)
                    .requestMatchers(
                        "/actuator/health", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()

                    // Admin-only
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                    // Chef o Admin
                    .requestMatchers("/chef/**")
                    .hasAnyRole("CHEF", "ADMIN")

                    // API general: autenticado
                    .requestMatchers("/api/**")
                    .authenticated()

                    // Todo lo demás: denegado
                    .anyRequest()
                    .denyAll())
        // Resource Server con JWT (Cognito)
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

    return http.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter() {
    var converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(
        jwt -> (Collection<GrantedAuthority>) extractAuthoritiesFromJwt(jwt));

    return converter;
  }

  private Collection<? extends GrantedAuthority> extractAuthoritiesFromJwt(Jwt jwt) {
    // 1) grupos -> roles Spring
    List<String> groups = jwt.getClaimAsStringList(groupsClaim);
    if (groups == null) groups = List.of();

    List<GrantedAuthority> roles =
        groups.stream()
            .map(g -> new SimpleGrantedAuthority("ROLE_" + g.toUpperCase()))
            .collect(Collectors.toList());

    // 2) (opcional) validar audience (app client id)
    if (requiredAudience != null && !requiredAudience.isBlank()) {
      List<String> aud = jwt.getAudience();
      if (aud == null || !aud.contains(requiredAudience)) {
        // Si querés fallar duro, podrías lanzar una excepción aquí.
        // O retornar lista vacía y que falle por autorización.
      }
    }
    return roles;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = new CorsConfiguration();
    cors.setAllowedOrigins(List.of("http://localhost:5173", "https://www.lacocinadelicia.com"));
    cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    cors.setAllowCredentials(false);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }
}
