// src/test/java/com/cocinadelicia/backend/order/domain/OrderStatusTransitionValidatorTest.java
package com.cocinadelicia.backend.order.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.model.enums.OrderStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class OrderStatusTransitionValidatorTest {

  @ParameterizedTest
  @CsvSource({
    "CREATED,PREPARING",
    "CREATED,CANCELED",
    "PREPARING,READY",
    "PREPARING,CANCELED",
    "READY,DELIVERED",
    // Permitimos READY->OUT_FOR_DELIVERY aunque la UI del sprint no lo use
    "READY,OUT_FOR_DELIVERY"
  })
  void validTransitions_doNotThrow(String current, String next) {
    assertDoesNotThrow(
        () ->
            OrderStatusTransitionValidator.validateOrThrow(
                OrderStatus.valueOf(current), OrderStatus.valueOf(next)));
  }

  @ParameterizedTest
  @CsvSource({"CREATED,DELIVERED", "READY,CREATED", "DELIVERED,READY", "CANCELED,CREATED"})
  void invalidTransitions_throwBadRequest(String current, String next) {
    var ex =
        assertThrows(
            BadRequestException.class,
            () ->
                OrderStatusTransitionValidator.validateOrThrow(
                    OrderStatus.valueOf(current), OrderStatus.valueOf(next)));
    assertEquals("INVALID_STATUS_TRANSITION", ex.code());
    assertTrue(ex.getMessage().contains(current));
    assertTrue(ex.getMessage().contains(next));
  }
}
