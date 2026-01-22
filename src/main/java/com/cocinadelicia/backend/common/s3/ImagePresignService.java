package com.cocinadelicia.backend.common.s3;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class ImagePresignService {

  private static final Set<String> ALLOWED_CONTENT_TYPES =
      Set.of("image/jpeg", "image/png", "image/webp");

  private final S3Presigner presigner;
  private final String bucket;
  private final String cdnBaseUrl;

  public ImagePresignService(
      S3Presigner presigner,
      @Value("${app.s3.images-bucket}") String bucket,
      @Value("${app.cdn.base-url}") String cdnBaseUrl) {
    this.presigner = presigner;
    this.bucket = bucket;
    this.cdnBaseUrl = cdnBaseUrl;
  }

  public PresignResult presignProductImageUpload(long productId, String contentType) {
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Tipo de imagen no permitido: " + contentType);
    }

    String ext =
        switch (contentType) {
          case "image/jpeg" -> "jpg";
          case "image/png" -> "png";
          case "image/webp" -> "webp";
          default -> "bin";
        };

    String objectKey = "products/" + productId + "/" + UUID.randomUUID() + "." + ext;

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket(bucket).key(objectKey).contentType(contentType).build();

    PutObjectPresignRequest presignRequest =
        PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(putObjectRequest)
            .build();

    String uploadUrl = presigner.presignPutObject(presignRequest).url().toString();
    String publicUrl = joinUrl(cdnBaseUrl, objectKey);

    return new PresignResult(uploadUrl, objectKey, publicUrl, Map.of("Content-Type", contentType));
  }

  private static String joinUrl(String base, String path) {
    if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
    if (path.startsWith("/")) path = path.substring(1);
    return base + "/" + path;
  }

  public record PresignResult(
      String uploadUrl, String objectKey, String publicUrl, Map<String, String> requiredHeaders) {}
}
