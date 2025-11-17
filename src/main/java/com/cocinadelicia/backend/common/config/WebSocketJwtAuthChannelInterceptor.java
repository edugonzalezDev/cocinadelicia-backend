package com.cocinadelicia.backend.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.*;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class WebSocketJwtAuthChannelInterceptor implements ChannelInterceptor {

  private final JwtDecoder jwtDecoder;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
      MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      return message;
    }

    // Solo nos interesa el frame CONNECT
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      List<String> authHeaders = accessor.getNativeHeader("Authorization");
      String authHeader = (authHeaders != null && !authHeaders.isEmpty())
        ? authHeaders.get(0)
        : null;

      if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
        log.warn("[WS] CONNECT sin Authorization Bearer: headers={}", authHeaders);
        throw new AccessDeniedException("Missing or invalid Authorization header in CONNECT");
      }

      String tokenValue = authHeader.substring(7);

      try {
        Jwt jwt = jwtDecoder.decode(tokenValue);
        Authentication authentication = jwtAuthenticationConverter.convert(jwt);
        if (authentication instanceof AbstractAuthenticationToken authToken) {
          authToken.setAuthenticated(true);
          accessor.setUser(authToken);
          SecurityContextHolder.getContext().setAuthentication(authToken);

          log.debug("[WS] Usuario autenticado en STOMP CONNECT: {}", authToken.getName());
        }
      } catch (JwtException ex) {
        log.warn("[WS] JWT inválido en CONNECT: {}", ex.getMessage());
        throw new AccessDeniedException("Invalid JWT token");
      }
    }

    return message;
  }

  // Excepción custom simple
  private static class AccessDeniedException extends MessagingException {
    public AccessDeniedException(String description) {
      super(description);
    }
  }
}
