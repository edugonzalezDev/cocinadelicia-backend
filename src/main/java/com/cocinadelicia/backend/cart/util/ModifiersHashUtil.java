package com.cocinadelicia.backend.cart.util;

import com.cocinadelicia.backend.cart.dto.CartItemModifierRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 * Utilidad para generar hash de modifiers para unicidad de items en carrito.
 * Sprint S07 - US01
 */
@Log4j2
public final class ModifiersHashUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private ModifiersHashUtil() {
    // Utility class
  }

  /**
   * Genera un hash SHA-256 del JSON de modifiers ordenado canónicamente.
   *
   * @param modifiers Lista de modifiers (puede ser null o vacía)
   * @return Hash SHA-256 como string hexadecimal, o "NONE" si no hay modifiers
   */
  public static String generateHash(List<CartItemModifierRequest> modifiers) {
    if (modifiers == null || modifiers.isEmpty()) {
      return "NONE";
    }

    try {
      // Ordenar modifiers por ID para garantizar consistencia
      var sortedModifiers =
          modifiers.stream()
              .sorted(Comparator.comparing(CartItemModifierRequest::modifierOptionId))
              .toList();

      // Serializar a JSON
      String json = OBJECT_MAPPER.writeValueAsString(sortedModifiers);

      // Generar hash SHA-256
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));

      // Convertir a hex
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();

    } catch (JsonProcessingException e) {
      log.error("Error serializing modifiers to JSON", e);
      throw new IllegalArgumentException("Invalid modifiers structure", e);
    } catch (NoSuchAlgorithmException e) {
      log.error("SHA-256 algorithm not available", e);
      throw new RuntimeException("Hash algorithm not available", e);
    }
  }

  /**
   * Serializa los modifiers a JSON para guardar en la base de datos.
   *
   * @param modifiers Lista de modifiers (puede ser null)
   * @return JSON string, o null si no hay modifiers
   */
  public static String toJson(List<CartItemModifierRequest> modifiers) {
    if (modifiers == null || modifiers.isEmpty()) {
      return null;
    }

    try {
      return OBJECT_MAPPER.writeValueAsString(modifiers);
    } catch (JsonProcessingException e) {
      log.error("Error serializing modifiers to JSON", e);
      throw new IllegalArgumentException("Invalid modifiers structure", e);
    }
  }
}
