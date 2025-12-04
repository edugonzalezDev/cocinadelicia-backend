package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para crear/actualizar categoría (admin)")
public record CategoryAdminRequest(

  @NotBlank
  @Size(max = 191)
  @Schema(description = "Nombre visible de la categoría", example = "Empanadas")
  String name,

  @NotBlank
  @Size(max = 191)
  @Schema(description = "Slug único (URL friendly)", example = "empanadas")
  String slug,

  @Size(max = 500)
  @Schema(description = "Descripción corta de la categoría", example = "Variedad de empanadas caseras")
  String description
) {}
