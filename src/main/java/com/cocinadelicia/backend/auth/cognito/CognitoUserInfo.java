package com.cocinadelicia.backend.auth.cognito;

import java.util.Set;
import lombok.Builder;
import lombok.Value;

/**
 * DTO interno para datos de usuario obtenidos de Cognito via AdminGetUser.
 *
 * <p>Usado para transferir información de perfil y grupos desde Cognito al servicio de usuarios.
 */
@Value
@Builder
public class CognitoUserInfo {
  String cognitoUserId; // sub attribute
  String email;
  String firstName;
  String lastName;
  String phone;
  Set<String> groups; // Grupos/roles en Cognito
}
