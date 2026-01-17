// src/main/java/com/cocinadelicia/backend/common/exception/NotFoundException.java
package com.cocinadelicia.backend.common.exception;

public class NotFoundException extends RuntimeException implements DomainException {
  private final String code;

  public NotFoundException(String message) {
    super(message);
    this.code = null;
  }

  public NotFoundException(String code, String message) {
    super(message);
    this.code = code;
  }

  @Override
  public String code() {
    return code;
  }
}
