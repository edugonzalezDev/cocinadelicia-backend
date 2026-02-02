package com.cocinadelicia.backend.catalog.admin.service.impl;

import com.cocinadelicia.backend.catalog.admin.service.AdminModifierGroupService;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminResponse;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminResponse;
import com.cocinadelicia.backend.product.model.ModifierGroup;
import com.cocinadelicia.backend.product.model.ModifierOption;
import com.cocinadelicia.backend.product.model.ModifierSelectionMode;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ModifierGroupRepository;
import com.cocinadelicia.backend.product.repository.ModifierOptionRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminModifierGroupServiceImpl implements AdminModifierGroupService {

  private final ModifierGroupRepository groupRepository;
  private final ModifierOptionRepository optionRepository;
  private final ProductVariantRepository productVariantRepository;
  private final ProductRepository productRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ModifierGroupAdminResponse> getByVariant(Long productVariantId) {
    ProductVariant variant =
        productVariantRepository
            .findById(productVariantId)
            .orElseThrow(
                () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada."));
    List<ModifierGroup> groups =
        groupRepository.findByProductVariant_IdOrderBySortOrderAscIdAsc(productVariantId);
    return groups.stream().map(this::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ModifierGroupAdminResponse getById(Long id) {
    ModifierGroup group =
        groupRepository
            .findByIdWithOptions(id)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_GROUP_NOT_FOUND", "Grupo no encontrado."));
    return toResponse(group);
  }

  @Override
  public ModifierGroupAdminResponse create(ModifierGroupAdminRequest request) {
    validateGroupPayload(request);

    ProductVariant variant =
        productVariantRepository
            .findById(request.productVariantId())
            .orElseThrow(
                () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada."));

    ModifierGroup group =
        ModifierGroup.builder()
            .productVariant(variant)
            .name(request.name())
            .minSelect(defaultInt(request.minSelect(), 0))
            .maxSelect(defaultInt(request.maxSelect(), 1))
            .selectionMode(resolveSelectionMode(request.selectionMode()))
            .requiredTotalQty(request.requiredTotalQty())
            .sortOrder(defaultInt(request.sortOrder(), 0))
            .active(request.active() == null ? true : request.active())
            .build();

    group = groupRepository.save(group);

    if (request.defaultOptionId() != null) {
      ModifierOption defaultOption =
          optionRepository
              .findById(request.defaultOptionId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "MODIFIER_OPTION_NOT_FOUND", "Opción por defecto no encontrada."));
      if (!defaultOption.getModifierGroup().getId().equals(group.getId())) {
        throw new BadRequestException(
            "DEFAULT_OPTION_INVALID", "La opción por defecto no pertenece al grupo.");
      }
      group.setDefaultOption(defaultOption);
    }

    return toResponse(groupRepository.save(group));
  }

  @Override
  public ModifierGroupAdminResponse update(Long id, ModifierGroupAdminRequest request) {
    ModifierGroup group =
        groupRepository
            .findByIdWithOptions(id)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_GROUP_NOT_FOUND", "Grupo no encontrado."));

    if (request.name() != null) group.setName(request.name());
    if (request.minSelect() != null) group.setMinSelect(request.minSelect());
    if (request.maxSelect() != null) group.setMaxSelect(request.maxSelect());
    if (request.selectionMode() != null)
      group.setSelectionMode(resolveSelectionMode(request.selectionMode()));
    if (request.requiredTotalQty() != null || request.selectionMode() != null) {
      group.setRequiredTotalQty(request.requiredTotalQty());
    }
    if (request.sortOrder() != null) group.setSortOrder(request.sortOrder());
    if (request.active() != null) group.setActive(request.active());

    validateGroupConstraints(group);

    if (request.defaultOptionId() != null) {
      ModifierOption defaultOption =
          optionRepository
              .findById(request.defaultOptionId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "MODIFIER_OPTION_NOT_FOUND", "Opción por defecto no encontrada."));
      if (!defaultOption.getModifierGroup().getId().equals(group.getId())) {
        throw new BadRequestException(
            "DEFAULT_OPTION_INVALID", "La opción por defecto no pertenece al grupo.");
      }
      group.setDefaultOption(defaultOption);
    } else if (request.defaultOptionId() == null && request.selectionMode() != null) {
      // si cambia selectionMode y no envía default, no tocamos
    }

    return toResponse(groupRepository.save(group));
  }

  @Override
  public void delete(Long id) {
    ModifierGroup group =
        groupRepository
            .findByIdWithOptions(id)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_GROUP_NOT_FOUND", "Grupo no encontrado."));
    group.setDefaultOption(null);
    groupRepository.delete(group);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ModifierGroupAdminResponse> getByProduct(Long productId) {
    if (!productRepository.existsById(productId)) {
      throw new NotFoundException("PRODUCT_NOT_FOUND", "Producto no encontrado.");
    }
    return groupRepository
        .findByProductVariant_Product_IdOrderBySortOrderAscIdAsc(productId)
        .stream()
        .map(this::toResponse)
        .toList();
  }

  @Override
  public ModifierGroupAdminResponse createForProduct(
      Long productId, ModifierGroupAdminRequest request) {
    ProductVariant variant =
        productVariantRepository
            .findById(request.productVariantId())
            .orElseThrow(
                () -> new NotFoundException("VARIANT_NOT_FOUND", "Variante no encontrada."));
    if (variant.getProduct() == null || !variant.getProduct().getId().equals(productId)) {
      throw new BadRequestException(
          "VARIANT_MISMATCH", "La variante no pertenece al producto indicado.");
    }
    return create(request);
  }

  private void validateGroupPayload(ModifierGroupAdminRequest request) {
    if (request.productVariantId() == null) {
      throw new BadRequestException("VARIANT_REQUIRED", "productVariantId es obligatorio.");
    }
    if (request.name() == null || request.name().isBlank()) {
      throw new BadRequestException("NAME_REQUIRED", "El nombre del grupo es obligatorio.");
    }
    validateGroupConstraints(
        ModifierGroup.builder()
            .minSelect(defaultInt(request.minSelect(), 0))
            .maxSelect(defaultInt(request.maxSelect(), 1))
            .selectionMode(resolveSelectionMode(request.selectionMode()))
            .requiredTotalQty(request.requiredTotalQty())
            .build());
  }

  private void validateGroupConstraints(ModifierGroup group) {
    if (group.getMinSelect() < 0 || group.getMaxSelect() < 0) {
      throw new BadRequestException(
          "MODIFIER_RANGE_INVALID", "minSelect/maxSelect no pueden ser negativos.");
    }
    if (group.getMinSelect() > group.getMaxSelect()) {
      throw new BadRequestException(
          "MODIFIER_RANGE_INVALID", "minSelect no puede ser mayor a maxSelect.");
    }
    if (group.getSelectionMode() == ModifierSelectionMode.SINGLE && group.getMaxSelect() > 1) {
      throw new BadRequestException(
          "MODIFIER_SINGLE_MAX", "selectionMode SINGLE requiere maxSelect <= 1.");
    }
    if (group.getSelectionMode() != ModifierSelectionMode.QTY
        && group.getRequiredTotalQty() != null) {
      throw new BadRequestException(
          "MODIFIER_REQUIRED_QTY_INVALID",
          "requiredTotalQty solo aplica cuando selectionMode=QTY.");
    }
  }

  private ModifierSelectionMode resolveSelectionMode(String mode) {
    if (mode == null) return ModifierSelectionMode.SINGLE;
    try {
      return ModifierSelectionMode.valueOf(mode);
    } catch (IllegalArgumentException ex) {
      throw new BadRequestException("INVALID_SELECTION_MODE", "selectionMode inválido");
    }
  }

  private int defaultInt(Integer value, int fallback) {
    return value == null ? fallback : value;
  }

  private ModifierGroupAdminResponse toResponse(ModifierGroup group) {
    List<ModifierOptionAdminResponse> options =
        group.getOptions() == null
            ? List.of()
            : group.getOptions().stream()
                .sorted(
                    Comparator.comparingInt(ModifierOption::getSortOrder)
                        .thenComparing(ModifierOption::getId))
                .map(
                    opt ->
                        new ModifierOptionAdminResponse(
                            opt.getId(),
                            group.getId(),
                            opt.getName(),
                            opt.getSortOrder(),
                            opt.isActive(),
                            opt.getPriceDelta(),
                            opt.isExclusive(),
                            opt.getLinkedProductVariant() != null
                                ? opt.getLinkedProductVariant().getId()
                                : null))
                .toList();

    return new ModifierGroupAdminResponse(
        group.getId(),
        group.getProductVariant() != null ? group.getProductVariant().getId() : null,
        group.getName(),
        group.getMinSelect(),
        group.getMaxSelect(),
        group.getSelectionMode().name(),
        group.getRequiredTotalQty(),
        group.getDefaultOption() != null ? group.getDefaultOption().getId() : null,
        group.getSortOrder(),
        group.isActive(),
        options);
  }
}
