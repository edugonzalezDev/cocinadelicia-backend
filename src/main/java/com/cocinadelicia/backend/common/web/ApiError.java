// src/main/java/com/cocinadelicia/backend/common/web/ApiError.java
package com.cocinadelicia.backend.common.web;

import java.time.Instant;

/** Error estándar. "code" es opcional y solo se envía para excepciones con código de dominio. */
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String code // <- NUEVO (puede ser null)
    ) {
  public static ApiError of(int status, String error, String message, String path) {
    return new ApiError(Instant.now(), status, error, message, path, null);
  }

  public static ApiError of(int status, String error, String message, String path, String code) {
    return new ApiError(Instant.now(), status, error, message, path, code);
  }
}
