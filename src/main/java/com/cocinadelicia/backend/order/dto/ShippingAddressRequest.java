package com.cocinadelicia.backend.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dirección de envío para pedidos con fulfillment = DELIVERY")
public record ShippingAddressRequest(

  @Schema(description = "Nombre del destinatario del envío", example = "Juan Pérez")
  @NotBlank
  @Size(max = 191)
  String name,

  @Schema(description = "Teléfono de contacto", example = "099123456")
  @NotBlank
  @Size(max = 50)
  @Pattern(regexp = "^[0-9 +()-]{6,50}$", message = "phone no válido")
  String phone,

  @Schema(description = "Dirección principal", example = "Av. Siempre Viva 742")
  @NotBlank
  @Size(max = 191)
  String line1,

  @Schema(description = "Dirección adicional (opcional)", example = "Apartamento 201")
  @Size(max = 191)
  String line2,

  @Schema(description = "Ciudad", example = "Montevideo")
  @NotBlank
  @Size(max = 100)
  String city,

  @Schema(description = "Región / Departamento", example = "Canelones")
  @Size(max = 100)
  String region,

  @Schema(description = "Código postal", example = "15000")
  @Size(max = 20)
  String postalCode,

  @Schema(description = "Referencia adicional", example = "Portón negro, timbre rojo")
  @Size(max = 191)
  String reference
) {}
