package com.cocinadelicia.backend.common.exception;

/**
 * Excepción de dominio para conflictos (409 Conflict).
 *
 * <p>Se lanza cuando se intenta crear un recurso que ya existe o hay conflicto de estado.
 */
public class ConflictException extends RuntimeException implements DomainException {
  private final String code;

  public ConflictException(String message) {
    super(message);
    this.code = null;
  }

  public ConflictException(String code, String message) {
    super(message);
    this.code = code;
  }

  @Override
  public String code() {
    return code;
  }
}
