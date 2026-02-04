package com.cocinadelicia.backend.auth.cognito;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

/**
 * Configuración de AWS Cognito Identity Provider.
 *
 * <p>Provee beans para operaciones administrativas de Cognito (AdminCreateUser,
 * AdminAddUserToGroup, etc.).
 */
@Configuration
@Log4j2
public class CognitoConfig {

  @Value("${cognito.region}")
  private String region;

  @Bean
  public CognitoIdentityProviderClient cognitoIdentityProviderClient() {
    Region awsRegion = Region.of(region);
    log.info("Initializing CognitoIdentityProviderClient with region: {}", region);

    return CognitoIdentityProviderClient.builder()
        .region(awsRegion)
        // Utiliza DefaultCredentialsProvider (variables de entorno, perfil AWS, IAM role, etc.)
        .build();
  }
}
