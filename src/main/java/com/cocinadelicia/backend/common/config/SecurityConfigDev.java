package com.cocinadelicia.backend.common.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cadena exclusiva para H2 en DEV. Deja pasar /h2-console/** y no interfiere con la cadena
 * principal JWT.
 */
@Configuration
@Profile("dev")
class SecurityConfigDev {

  @Bean
  @Order(0) // esta cadena se evalúa antes que la principal
  SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher(PathRequest.toH2Console()) // solo aplica a /h2-console/**
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
        .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
    // OJO: no formLogin, no oauth2 aquí. Solo excepciona H2.
    return http.build();
  }
}
