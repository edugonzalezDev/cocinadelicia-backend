// src/main/java/com/cocinadelicia/backend/common/exception/DomainException.java
package com.cocinadelicia.backend.common.exception;

public interface DomainException {
  String code(); // ej.: "ORDER_ITEMS_EMPTY", "PRICE_NOT_FOUND"
}
