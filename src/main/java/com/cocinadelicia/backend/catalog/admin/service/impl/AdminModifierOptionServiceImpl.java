package com.cocinadelicia.backend.catalog.admin.service.impl;

import com.cocinadelicia.backend.catalog.admin.service.AdminModifierOptionService;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminRequest;
import com.cocinadelicia.backend.product.dto.ModifierOptionAdminResponse;
import com.cocinadelicia.backend.product.model.ModifierGroup;
import com.cocinadelicia.backend.product.model.ModifierOption;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.ModifierGroupRepository;
import com.cocinadelicia.backend.product.repository.ModifierOptionRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminModifierOptionServiceImpl implements AdminModifierOptionService {

  private final ModifierGroupRepository groupRepository;
  private final ModifierOptionRepository optionRepository;
  private final ProductVariantRepository productVariantRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ModifierOptionAdminResponse> getByGroup(Long groupId) {
    ModifierGroup group =
        groupRepository
            .findByIdWithOptions(groupId)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_GROUP_NOT_FOUND", "Grupo no encontrado."));
    return group.getOptions().stream()
        .sorted(
            Comparator.comparingInt(ModifierOption::getSortOrder)
                .thenComparing(ModifierOption::getId))
        .map(this::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ModifierOptionAdminResponse getById(Long id) {
    ModifierOption option =
        optionRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_OPTION_NOT_FOUND", "Opción no encontrada."));
    return toResponse(option);
  }

  @Override
  public ModifierOptionAdminResponse create(Long groupId, ModifierOptionAdminRequest request) {
    ModifierGroup group =
        groupRepository
            .findByIdWithOptions(groupId)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_GROUP_NOT_FOUND", "Grupo no encontrado."));

    validateOptionPayload(request);

    ProductVariant linkedVariant = null;
    if (request.linkedProductVariantId() != null) {
      linkedVariant =
          productVariantRepository
              .findById(request.linkedProductVariantId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "LINKED_VARIANT_NOT_FOUND", "Variante linkeada no encontrada."));
    }

    ModifierOption option =
        ModifierOption.builder()
            .modifierGroup(group)
            .name(request.name())
            .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
            .active(request.active() == null ? true : request.active())
            .priceDelta(request.priceDelta())
            .exclusive(request.exclusive() != null ? request.exclusive() : false)
            .linkedProductVariant(linkedVariant)
            .build();

    option = optionRepository.save(option);

    return toResponse(option);
  }

  @Override
  public ModifierOptionAdminResponse update(Long id, ModifierOptionAdminRequest request) {
    ModifierOption option =
        optionRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_OPTION_NOT_FOUND", "Opción no encontrada."));

    if (request.name() != null) option.setName(request.name());
    if (request.sortOrder() != null) option.setSortOrder(request.sortOrder());
    if (request.priceDelta() != null) option.setPriceDelta(request.priceDelta());
    if (request.exclusive() != null) option.setExclusive(request.exclusive());
    if (request.active() != null) option.setActive(request.active());

    if (request.linkedProductVariantId() != null) {
      ProductVariant linkedVariant =
          productVariantRepository
              .findById(request.linkedProductVariantId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "LINKED_VARIANT_NOT_FOUND", "Variante linkeada no encontrada."));
      option.setLinkedProductVariant(linkedVariant);
    }

    option = optionRepository.save(option);

    ModifierGroup group = option.getModifierGroup();
    if (group.getDefaultOption() != null
        && group.getDefaultOption().getId().equals(option.getId())) {
      if (!option.isActive()) {
        group.setDefaultOption(null);
        groupRepository.save(group);
      }
    }

    return toResponse(option);
  }

  @Override
  public void delete(Long id) {
    ModifierOption option =
        optionRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException("MODIFIER_OPTION_NOT_FOUND", "Opción no encontrada."));
    ModifierGroup group = option.getModifierGroup();
    if (group.getDefaultOption() != null && group.getDefaultOption().getId().equals(id)) {
      group.setDefaultOption(null);
      groupRepository.save(group);
    }
    optionRepository.delete(option);
  }

  private void validateOptionPayload(ModifierOptionAdminRequest request) {
    if (request.name() == null || request.name().isBlank()) {
      throw new BadRequestException("NAME_REQUIRED", "El nombre de la opción es obligatorio.");
    }
  }

  private ModifierOptionAdminResponse toResponse(ModifierOption option) {
    return new ModifierOptionAdminResponse(
        option.getId(),
        option.getModifierGroup() != null ? option.getModifierGroup().getId() : null,
        option.getName(),
        option.getSortOrder(),
        option.isActive(),
        option.getPriceDelta(),
        option.isExclusive(),
        option.getLinkedProductVariant() != null ? option.getLinkedProductVariant().getId() : null);
  }
}
