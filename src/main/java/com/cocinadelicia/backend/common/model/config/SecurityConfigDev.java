package com.cocinadelicia.backend.common.model.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev") // <-- opcional pero recomendado: solo en perfil dev
class SecurityConfigDev {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1) Permitir la consola H2 sin autenticación
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .anyRequest().authenticated()
                )
                // 2) Desactivar CSRF para la consola H2
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathRequest.toH2Console())
                )
                // 3) Permitir iframes desde el mismo origen (necesario para H2)
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                // 4) Seguir usando formLogin para el resto
                .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
