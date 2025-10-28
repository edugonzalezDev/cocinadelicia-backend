package com.cocinadelicia.backend.user.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponseDTO {
    Long id;
    String cognitoUserId;
    String email;
    String firstName;
    String lastName;
    String phone;
    Set<String> roles; // 'ADMIN','CHEF','COURIER','CUSTOMER'
}
