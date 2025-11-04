// src/main/java/com/cocinadelicia/backend/common/exception/BadRequestException.java
package com.cocinadelicia.backend.common.exception;

public class BadRequestException extends RuntimeException implements DomainException {
  private final String code;

  public BadRequestException(String message) {
    super(message);
    this.code = null;
  }

  public BadRequestException(String code, String message) {
    super(message);
    this.code = code;
  }

  @Override
  public String code() {
    return code;
  }
}
