package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para crear/actualizar variante de producto")
public record ProductVariantAdminRequest(

  @NotBlank
  @Size(max = 191)
  @Schema(description = "Nombre de la variante", example = "Carne suave")
  String name,

  @Size(max = 191)
  @Schema(description = "SKU único (opcional)", example = "EMP-CARNE-SUAVE")
  String sku,

  @Schema(description = "Si la variante está activa", example = "true")
  Boolean isActive,

  @Schema(description = "Si maneja stock", example = "true")
  Boolean managesStock,

  @Min(0)
  @Schema(description = "Cantidad en stock (si maneja stock)", example = "12")
  Integer stockQuantity
) {}
