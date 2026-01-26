package com.cocinadelicia.backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request para cancelar una orden")
public record OrderCancelRequest(
    @Schema(description = "Razón de la cancelación (opcional)", example = "Cambié de opinión")
        @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
        String reason) {}
