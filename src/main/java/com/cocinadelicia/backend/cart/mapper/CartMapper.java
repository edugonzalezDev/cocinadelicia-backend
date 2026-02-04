package com.cocinadelicia.backend.cart.mapper;

import com.cocinadelicia.backend.cart.dto.CartItemModifierResponse;
import com.cocinadelicia.backend.cart.dto.CartItemResponse;
import com.cocinadelicia.backend.cart.dto.CartResponse;
import com.cocinadelicia.backend.cart.model.Cart;
import com.cocinadelicia.backend.cart.model.CartItem;
import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.order.port.PriceQueryPort;
import com.cocinadelicia.backend.product.model.ModifierOption;
import com.cocinadelicia.backend.product.repository.ModifierOptionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entidades Cart/CartItem a DTOs de response.
 * Sprint S07 - US01
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class CartMapper {

  private final PriceQueryPort priceQueryPort;
  private final ModifierOptionRepository modifierOptionRepository;
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Convierte una entidad Cart a CartResponse con precios actuales.
   *
   * @param cart Entidad Cart
   * @return CartResponse con items y subtotal calculado
   */
  public CartResponse toResponse(Cart cart) {
    if (cart == null) {
      return null;
    }

    List<CartItemResponse> itemResponses =
        cart.getItems().stream().map(this::toItemResponse).toList();

    // Calcular subtotal sumando lineTotal de todos los items
    BigDecimal subtotal =
        itemResponses.stream()
            .map(CartItemResponse::lineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    // Contar total de items (suma de cantidades)
    int itemCount = itemResponses.stream().mapToInt(CartItemResponse::quantity).sum();

    return new CartResponse(
        cart.getId(),
        cart.getUser().getId(),
        itemResponses,
        itemCount,
        CurrencyCode.UYU, // TODO: Hacerlo configurable si es necesario
        subtotal,
        cart.getCreatedAt(),
        cart.getUpdatedAt());
  }

  /**
   * Convierte un CartItem a CartItemResponse con precio actual y modifiers.
   *
   * @param item CartItem entity
   * @return CartItemResponse con precios calculados
   */
  public CartItemResponse toItemResponse(CartItem item) {
    // Obtener precio vigente de la variante
    BigDecimal unitPrice =
        priceQueryPort
            .findActivePriceByVariantId(item.getProductVariant().getId())
            .orElse(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);

    // Calcular total base (sin modifiers)
    BigDecimal baseLineTotal =
        unitPrice
            .multiply(BigDecimal.valueOf(item.getQuantity()))
            .setScale(2, RoundingMode.HALF_UP);

    // Parsear y calcular modifiers
    List<CartItemModifierResponse> modifierResponses = parseModifiers(item.getModifiersJson());
    BigDecimal modifiersTotal = calculateModifiersTotal(modifierResponses);

    // Total de la línea = base + modifiers
    BigDecimal lineTotal = baseLineTotal.add(modifiersTotal).setScale(2, RoundingMode.HALF_UP);

    return new CartItemResponse(
        item.getId(),
        item.getProduct().getId(),
        item.getProductName(),
        item.getProductVariant().getId(),
        item.getVariantName(),
        item.getQuantity(),
        unitPrice,
        baseLineTotal,
        modifiersTotal,
        lineTotal,
        modifierResponses.isEmpty() ? null : modifierResponses);
  }

  /**
   * Parsea el JSON de modifiers y genera lista de CartItemModifierResponse con precios.
   *
   * @param modifiersJson JSON string con modifiers
   * @return Lista de CartItemModifierResponse (vacía si no hay modifiers)
   */
  private List<CartItemModifierResponse> parseModifiers(String modifiersJson) {
    if (modifiersJson == null || modifiersJson.isBlank()) {
      return List.of();
    }

    try {
      // Parsear JSON a lista de mapas
      List<Map<String, Object>> modifiersList =
          OBJECT_MAPPER.readValue(modifiersJson, new TypeReference<>() {});

      // Extraer IDs de opciones
      List<Long> optionIds =
          modifiersList.stream()
              .map(m -> ((Number) m.get("modifierOptionId")).longValue())
              .toList();

      // Obtener opciones de la DB
      Map<Long, ModifierOption> optionsById =
          modifierOptionRepository.findAllById(optionIds).stream()
              .collect(Collectors.toMap(ModifierOption::getId, o -> o));

      // Construir responses
      List<CartItemModifierResponse> responses = new ArrayList<>();
      for (Map<String, Object> modMap : modifiersList) {
        Long optionId = ((Number) modMap.get("modifierOptionId")).longValue();
        Integer quantity = ((Number) modMap.get("quantity")).intValue();

        ModifierOption option = optionsById.get(optionId);
        if (option == null) {
          log.warn("ModifierOption {} not found in DB, skipping", optionId);
          continue;
        }

        BigDecimal priceDelta = option.getPriceDelta().setScale(2, RoundingMode.HALF_UP);
        BigDecimal total =
            priceDelta.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

        responses.add(
            new CartItemModifierResponse(optionId, option.getName(), quantity, priceDelta, total));
      }

      return responses;

    } catch (Exception e) {
      log.error("Error parsing modifiers JSON: {}", modifiersJson, e);
      return List.of();
    }
  }

  /**
   * Calcula el total de modifiers sumando los totales individuales.
   *
   * @param modifiers Lista de modifiers
   * @return Total de modifiers
   */
  private BigDecimal calculateModifiersTotal(List<CartItemModifierResponse> modifiers) {
    if (modifiers == null || modifiers.isEmpty()) {
      return BigDecimal.ZERO;
    }

    return modifiers.stream()
        .map(CartItemModifierResponse::total)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }
}
