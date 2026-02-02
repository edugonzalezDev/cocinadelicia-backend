package com.cocinadelicia.backend.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Schema(description = "Payload para crear o actualizar una opción de modificador")
public record ModifierOptionAdminRequest(
    @Schema(description = "Nombre de la opción", example = "Puré de papas")
        @NotBlank(message = "name es obligatorio") String name,
    @Schema(description = "Orden de despliegue", example = "0") Integer sortOrder,
    @Schema(description = "Activo", example = "true") Boolean active,
    @Schema(description = "Delta de precio a aplicar", example = "50.00") BigDecimal priceDelta,
    @Schema(description = "Indica si es exclusiva dentro del grupo", example = "false")
        Boolean exclusive,
    @Schema(description = "Id de variante linkeada", example = "200")
        Long linkedProductVariantId) {}
