package com.cocinadelicia.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Broker simple en memoria para destinos que empiezan con /topic
    registry.enableSimpleBroker("/topic");

    // Prefijo para destinos de aplicación (mensajes que procesa el backend)
    // Ej: el cliente envía a /app/order.update
    registry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      // Endpoint donde el cliente se conecta vía WebSocket/SockJS
      .addEndpoint("/ws")
      // Orígenes permitidos (podés afinarlo después si querés)
      .setAllowedOriginPatterns(
        "http://localhost:*",
        "http://127.0.0.1:*",
        "https://*.onrender.com",
        "https://*.lacocinadelicia.com"
      )
      // Habilitamos SockJS para fallback (HTTP long polling, etc.)
      .withSockJS();
  }
}
