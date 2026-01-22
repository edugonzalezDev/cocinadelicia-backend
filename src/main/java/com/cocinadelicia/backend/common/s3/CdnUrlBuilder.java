package com.cocinadelicia.backend.common.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CdnUrlBuilder {

  private final String cdnBaseUrl;

  public CdnUrlBuilder(@Value("${app.cdn.base-url:}") String cdnBaseUrl) {
    this.cdnBaseUrl = cdnBaseUrl;
  }

  public String toPublicUrl(String objectKey) {
    if (objectKey == null || objectKey.isBlank()) return null;

    // ✅ si no hay base url configurada, devolvemos el key para debug/fallback
    if (cdnBaseUrl == null || cdnBaseUrl.isBlank()) return objectKey;

    String base = cdnBaseUrl;
    String path = objectKey;

    if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
    if (path.startsWith("/")) path = path.substring(1);

    return base + "/" + path;
  }
}
