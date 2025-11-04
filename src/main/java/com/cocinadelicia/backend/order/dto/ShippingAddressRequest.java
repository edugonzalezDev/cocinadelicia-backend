package com.cocinadelicia.backend.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ShippingAddressRequest(
    @NotBlank @Size(max = 191) String name,
    @NotBlank @Size(max = 50) @Pattern(regexp = "^[0-9 +()-]{6,50}$", message = "phone no válido")
        String phone,
    @NotBlank @Size(max = 191) String line1,
    @Size(max = 191) String line2,
    @NotBlank @Size(max = 100) String city,
    @Size(max = 100) String region,
    @Size(max = 20) String postalCode,
    @Size(max = 191) String reference) {}
