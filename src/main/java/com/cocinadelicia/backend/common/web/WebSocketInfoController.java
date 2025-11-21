package com.cocinadelicia.backend.common.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
@Tag(name = "system", description = "Endpoints de sistema y metadatos")
public class WebSocketInfoController {

  @Schema(
    description = "Información básica sobre el endpoint WebSocket/STOMP"
  )
  public record WebSocketInfoResponse(
    @Schema(example = "/ws")
    String endpoint,

    @Schema(example = "/topic/orders")
    String ordersTopic,

    @Schema(example = "/app")
    String applicationPrefix,

    @Schema(example = "ORDER_UPDATED")
    String exampleEventType
  ) {}

  @GetMapping("/ws-info")
  @Operation(
    summary = "Información del endpoint WebSocket",
    description = """
      Devuelve los valores configurados para el endpoint STOMP:
      - endpoint: path para conectar (ej. ws://host:port/ws)
      - ordersTopic: tópico donde se publican actualizaciones de pedidos
      - applicationPrefix: prefijo para destinos de aplicación
      - exampleEventType: tipo de evento estándar para actualizaciones de pedido
      """
  )
  public ResponseEntity<WebSocketInfoResponse> getWebSocketInfo() {
    return ResponseEntity.ok(
      new WebSocketInfoResponse(
        "/ws",
        "/topic/orders",
        "/app",
        "ORDER_UPDATED"
      )
    );
  }
}
