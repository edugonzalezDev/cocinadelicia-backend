package com.cocinadelicia.backend.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final WebSocketJwtAuthChannelInterceptor webSocketJwtAuthChannelInterceptor;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setAllowedOriginPatterns(
            "http://localhost:*",
            "https://localhost:*",
            "https://192.168.1.4:*",
            "http://127.0.0.1:*",
            "https://*.onrender.com",
            "https://*.lacocinadelicia.com",
            "https://cocinadelicia-frontend.netlify.app")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    // 🔴 Aquí enchufamos el interceptor que valida JWT en CONNECT
    registration.interceptors(webSocketJwtAuthChannelInterceptor);
  }
}
