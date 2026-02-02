package com.cocinadelicia.backend.order.domain.validator;

import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.order.dto.OrderItemModifierRequest;
import com.cocinadelicia.backend.order.model.OrderItemModifier;
import com.cocinadelicia.backend.order.port.PriceQueryPort;
import com.cocinadelicia.backend.product.model.ModifierGroup;
import com.cocinadelicia.backend.product.model.ModifierOption;
import com.cocinadelicia.backend.product.model.ProductVariant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Validador de selecciones de modificadores.
 *
 * <p>Reglas validadas: 1. Cantidad de opciones en rango [minSelect, maxSelect] 2. Si
 * requiredTotalQty, cantidad total debe coincidir 3. Si isExclusive, solo una opción del grupo
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class ModifierSelectionValidator {

  private static final BigDecimal ZERO = BigDecimal.ZERO;
  private final PriceQueryPort priceQueryPort;

  /**
   * Valida y procesa modificadores para un item de orden.
   *
   * @param variant La variante que contiene los grupos de modificadores
   * @param requests Las solicitudes de modificador
   * @return Lista de OrderItemModifier con snapshots
   * @throws BadRequestException si validación falla
   */
  public List<OrderItemModifier> processModifiersForItem(
      ProductVariant variant, List<OrderItemModifierRequest> requests) {

    if (requests == null || requests.isEmpty()) {
      return List.of();
    }

    List<OrderItemModifier> result = new ArrayList<>();

    // Mapeo opción -> grupo
    Map<Long, ModifierGroup> optionToGroup =
        variant.getModifierGroups().stream()
            .flatMap(g -> g.getOptions().stream().map(o -> Map.entry(o.getId(), g)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    // Agrupar por ModifierGroup
    Map<ModifierGroup, List<OrderItemModifierRequest>> byGroup =
        requests.stream()
            .collect(
                Collectors.groupingBy(
                    req -> {
                      ModifierGroup group = optionToGroup.get(req.modifierOptionId());
                      if (group == null) {
                        throw new BadRequestException(
                            "MODIFIER_OPTION_NOT_FOUND",
                            "Opción de modificador " + req.modifierOptionId() + " no existe");
                      }
                      return group;
                    }));

    // Validar por grupo
    for (Map.Entry<ModifierGroup, List<OrderItemModifierRequest>> entry : byGroup.entrySet()) {
      ModifierGroup group = entry.getKey();
      List<OrderItemModifierRequest> selections = entry.getValue();

      validateGroup(group, selections);
    }

    // Crear OrderItemModifier
    for (OrderItemModifierRequest req : requests) {
      ModifierOption option =
          variant.getModifierGroups().stream()
              .flatMap(g -> g.getOptions().stream())
              .filter(o -> o.getId().equals(req.modifierOptionId()))
              .findFirst()
              .orElseThrow(
                  () ->
                      new BadRequestException(
                          "MODIFIER_OPTION_NOT_FOUND",
                          "Opción " + req.modifierOptionId() + " no existe"));

      BigDecimal priceDelta = option.getPriceDelta() != null ? option.getPriceDelta() : ZERO;
      BigDecimal linkedUnitPrice = ZERO;
      Long linkedVariantId = null;

      if (option.getLinkedProductVariant() != null) {
        linkedVariantId = option.getLinkedProductVariant().getId();
        linkedUnitPrice = priceQueryPort.findActivePriceByVariantId(linkedVariantId).orElse(ZERO);
      }

      BigDecimal totalPrice =
          priceDelta
              .add(linkedUnitPrice)
              .multiply(BigDecimal.valueOf(req.quantity()))
              .setScale(2, RoundingMode.HALF_UP);

      OrderItemModifier modifier =
          OrderItemModifier.builder()
              .modifierOption(option)
              .quantity(req.quantity())
              .optionNameSnapshot(option.getName())
              .priceDeltaSnapshot(priceDelta)
              .unitPriceSnapshot(linkedUnitPrice)
              .totalPriceSnapshot(totalPrice)
              .linkedProductVariantIdSnapshot(linkedVariantId)
              .build();

      result.add(modifier);
    }

    log.debug(
        "ModifierSelectionValidator: processed {} modifiers for variant {}",
        result.size(),
        variant.getId());

    return result;
  }

  private void validateGroup(ModifierGroup group, List<OrderItemModifierRequest> selections) {
    // Regla 1: cantidad en rango
    if (selections.size() < group.getMinSelect() || selections.size() > group.getMaxSelect()) {
      throw new BadRequestException(
          "MODIFIER_SELECTION_INVALID",
          String.format(
              "Grupo '%s': seleccionar entre %d y %d opciones (tienes: %d)",
              group.getName(), group.getMinSelect(), group.getMaxSelect(), selections.size()));
    }

    // Regla 2: requiredTotalQty
    if (group.getRequiredTotalQty() != null) {
      int totalQty = selections.stream().mapToInt(OrderItemModifierRequest::quantity).sum();
      if (totalQty != group.getRequiredTotalQty()) {
        throw new BadRequestException(
            "MODIFIER_QTY_INVALID",
            String.format(
                "Grupo '%s': cantidad total debe ser %d (tienes: %d)",
                group.getName(), group.getRequiredTotalQty(), totalQty));
      }
    }

    // Regla 3: exclusividad
    List<ModifierOption> selectedOptions =
        selections.stream()
            .map(
                req ->
                    group.getOptions().stream()
                        .filter(o -> o.getId().equals(req.modifierOptionId()))
                        .findFirst()
                        .orElseThrow())
            .toList();

    boolean hasExclusive = selectedOptions.stream().anyMatch(ModifierOption::isExclusive);
    if (hasExclusive && selections.size() > 1) {
      throw new BadRequestException(
          "MODIFIER_EXCLUSIVE_CONFLICT",
          "Una opción exclusiva no puede combinarse con otras del mismo grupo");
    }
  }
}
