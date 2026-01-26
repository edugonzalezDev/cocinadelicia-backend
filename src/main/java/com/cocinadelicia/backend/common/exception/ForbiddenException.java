package com.cocinadelicia.backend.common.exception;

/**
 * Excepción para indicar que el usuario no tiene permisos para acceder a un recurso.
 * Se mapea a HTTP 403 Forbidden.
 */
public class ForbiddenException extends RuntimeException implements DomainException {
  private final String code;

  public ForbiddenException(String message) {
    super(message);
    this.code = "FORBIDDEN";
  }

  public ForbiddenException(String code, String message) {
    super(message);
    this.code = code;
  }

  @Override
  public String code() {
    return code;
  }
}
