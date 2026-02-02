package com.cocinadelicia.backend.order.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Datos de cliente y envío asociados a una orden")
public record OrderAdminCustomerResponse(
    @Schema(description = "Id del usuario", example = "123") Long userId,
    @Schema(description = "Email del usuario", example = "cliente@example.com") String userEmail,
    @Schema(description = "Nombre") String firstName,
    @Schema(description = "Apellido") String lastName,
    @Schema(description = "Teléfono") String phone,
    @Schema(description = "Direcciones guardadas del usuario")
        List<CustomerAddressResponse> addresses,
    @Schema(description = "Snapshot de envío del pedido")
        ShippingSnapshotResponse shippingSnapshot) {

  @Schema(description = "Dirección guardada del usuario")
  public record CustomerAddressResponse(
      @Schema(description = "Id de dirección", example = "55") Long id,
      @Schema(description = "Etiqueta", example = "Casa") String label,
      @Schema(description = "Dirección línea 1") String line1,
      @Schema(description = "Dirección línea 2") String line2,
      @Schema(description = "Ciudad") String city,
      @Schema(description = "Región/Departamento") String region,
      @Schema(description = "Código postal") String postalCode,
      @Schema(description = "Referencia") String reference) {}

  @Schema(description = "Snapshot de envío del pedido")
  public record ShippingSnapshotResponse(
      @Schema(description = "Nombre de envío") String name,
      @Schema(description = "Teléfono de envío") String phone,
      @Schema(description = "Dirección línea 1") String line1,
      @Schema(description = "Dirección línea 2") String line2,
      @Schema(description = "Ciudad") String city,
      @Schema(description = "Región/Departamento") String region,
      @Schema(description = "Código postal") String postalCode,
      @Schema(description = "Referencia") String reference) {}
}
