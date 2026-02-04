package com.cocinadelicia.backend.auth.cognito;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

/**
 * Cliente para operaciones administrativas de AWS Cognito.
 *
 * <p>Maneja creación de usuarios, asignación a grupos y otras operaciones admin del User Pool.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class CognitoAdminClient {

  private final CognitoIdentityProviderClient cognitoClient;

  @Value("${cognito.user-pool-id}")
  private String userPoolId;

  /**
   * Crea un usuario en Cognito usando AdminCreateUser.
   *
   * <p>El usuario recibirá un email de invitación con credenciales temporales.
   *
   * @param email email del usuario (usado como username)
   * @param firstName nombre (opcional)
   * @param lastName apellido (opcional)
   * @param phone teléfono (opcional)
   * @return cognitoUserId (sub claim del usuario creado)
   * @throws ConflictException si el usuario ya existe en Cognito
   * @throws RuntimeException si ocurre otro error de Cognito
   */
  public String createUser(String email, String firstName, String lastName, String phone) {
    log.info("Creating user in Cognito with email: {}", email);

    AdminCreateUserRequest.Builder requestBuilder =
        AdminCreateUserRequest.builder()
            .userPoolId(userPoolId)
            .username(email) // Usamos email como username
            .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
            .forceAliasCreation(false);

    // Agregar atributos opcionales
    if (firstName != null && !firstName.isBlank()) {
      requestBuilder.userAttributes(
          AttributeType.builder().name("given_name").value(firstName).build());
    }
    if (lastName != null && !lastName.isBlank()) {
      requestBuilder.userAttributes(
          AttributeType.builder().name("family_name").value(lastName).build());
    }
    if (phone != null && !phone.isBlank()) {
      requestBuilder.userAttributes(
          AttributeType.builder().name("phone_number").value(phone).build());
    }

    // Atributo email (verificado)
    requestBuilder.userAttributes(
        AttributeType.builder().name("email").value(email).build(),
        AttributeType.builder().name("email_verified").value("true").build());

    try {
      AdminCreateUserResponse response = cognitoClient.adminCreateUser(requestBuilder.build());

      String cognitoUserId =
          response.user().attributes().stream()
              .filter(attr -> "sub".equals(attr.name()))
              .map(AttributeType::value)
              .findFirst()
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Cognito user created but 'sub' attribute not found"));

      log.info("User created successfully in Cognito with sub: {}", cognitoUserId);
      return cognitoUserId;

    } catch (UsernameExistsException e) {
      log.warn("User with email {} already exists in Cognito", email);
      throw new ConflictException(
          "USER_EXISTS_IN_COGNITO",
          "El usuario ya existe en Cognito. Considera usar 'Importar usuario existente'.");

    } catch (InvalidParameterException e) {
      log.error("Invalid parameter when creating user in Cognito: {}", e.getMessage());
      throw new BadRequestException(
          "INVALID_COGNITO_PARAMETER", "Parámetros inválidos: " + e.awsErrorDetails().errorMessage());

    } catch (CognitoIdentityProviderException e) {
      log.error("Cognito error creating user: {} - {}", e.awsErrorDetails().errorCode(), e.getMessage());
      throw new RuntimeException("Error al crear usuario en Cognito: " + e.awsErrorDetails().errorMessage(), e);
    }
  }

  /**
   * Agrega un usuario a un grupo de Cognito.
   *
   * @param username username del usuario (email)
   * @param groupName nombre del grupo (ej: ADMIN, CUSTOMER, CHEF, COURIER)
   * @throws RuntimeException si ocurre error de Cognito
   */
  public void addUserToGroup(String username, String groupName) {
    log.info("Adding user {} to Cognito group: {}", username, groupName);

    AdminAddUserToGroupRequest request =
        AdminAddUserToGroupRequest.builder()
            .userPoolId(userPoolId)
            .username(username)
            .groupName(groupName)
            .build();

    try {
      cognitoClient.adminAddUserToGroup(request);
      log.debug("User {} successfully added to group {}", username, groupName);

    } catch (ResourceNotFoundException e) {
      log.error("User or group not found: {} / {}", username, groupName);
      throw new RuntimeException(
          "Usuario o grupo no encontrado en Cognito: " + e.awsErrorDetails().errorMessage(), e);

    } catch (CognitoIdentityProviderException e) {
      log.error("Cognito error adding user to group: {} - {}", e.awsErrorDetails().errorCode(), e.getMessage());
      throw new RuntimeException(
          "Error al asignar usuario a grupo en Cognito: " + e.awsErrorDetails().errorMessage(), e);
    }
  }
}
