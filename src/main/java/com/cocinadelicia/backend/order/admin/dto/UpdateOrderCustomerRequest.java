package com.cocinadelicia.backend.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request para cambiar el cliente asociado a una orden")
public record UpdateOrderCustomerRequest(
    @Schema(
            description = "Email del nuevo cliente",
            example = "cliente@example.com",
            required = true)
        @NotBlank(message = "El email del cliente es obligatorio") @Email(message = "Debe ser un email válido") String userEmail) {}
