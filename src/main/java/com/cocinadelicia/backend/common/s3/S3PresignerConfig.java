package com.cocinadelicia.backend.common.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3PresignerConfig {

  @Bean
  public S3Presigner s3Presigner(@Value("${app.s3.region:us-east-1}") String region) {
    return S3Presigner.builder()
        .region(Region.of(region))
        // Credenciales: IAM Role / env vars / instance profile (default chain)
        .build();
  }
}
