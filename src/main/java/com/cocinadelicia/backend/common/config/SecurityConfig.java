package com.cocinadelicia.backend.common.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

@Configuration
@EnableMethodSecurity
// ⚠️ SIN @Profile aquí, así aplica en dev y prod
class SecurityConfig {

  @Value("${cdd.security.groups-claim:cognito:groups}")
  private String groupsClaim;

  @Value("${cdd.security.required-audience:}")
  private String requiredAudience;

  @Bean
  SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
    http
        // 👇 Habilita CORS (usará el bean CorsConfigurationSource)
        .cors(cors -> {})
        .csrf(csrf -> csrf.disable())
        .headers(h -> h.frameOptions(frame -> frame.sameOrigin())) // H2 en dev
        .authorizeHttpRequests(
            auth ->
                auth
                    // 👇 MUY IMPORTANTE: dejar pasar preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

                    // públicos
                    .requestMatchers(
                        "/actuator/health",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/h2-console/**")
                    .permitAll()

                    // Admin-only
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    // Chef o Admin
                    .requestMatchers("/chef/**")
                    .hasAnyRole("CHEF", "ADMIN")

                    // Tu API (requiere token)
                    .requestMatchers("/api/**")
                    .authenticated()

                    // resto denegado
                    .anyRequest()
                    .denyAll())
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
    List<String> groups = jwt.getClaimAsStringList(groupsClaim);
    if (groups == null) groups = List.of();
    var roles =
        groups.stream()
            .map(g -> new SimpleGrantedAuthority("ROLE_" + g.toUpperCase()))
            .collect(Collectors.toList());

    if (requiredAudience != null && !requiredAudience.isBlank()) {
      List<String> aud = jwt.getAudience();
      if (aud == null || !aud.contains(requiredAudience)) {
        // opción: lanzar excepción si querés “fallar duro”
      }
    }
    return roles;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cors = new CorsConfiguration();
    // Para dev es más práctico usar patterns por puerto
    cors.setAllowedOriginPatterns(
        List.of(
            "http://localhost:*",
            "https://www.lacocinadelicia.com",
            "https://cocinadelicia-frontend.netlify.app/"));
    cors.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    cors.setAllowedHeaders(
        Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
    cors.setExposedHeaders(List.of("Location"));
    cors.setAllowCredentials(false);
    cors.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cors);
    return source;
  }
}
