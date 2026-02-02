package com.cocinadelicia.backend.catalog.admin.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.cocinadelicia.backend.catalog.admin.service.impl.AdminModifierGroupServiceImpl;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.product.dto.ModifierGroupAdminRequest;
import com.cocinadelicia.backend.product.repository.ModifierGroupRepository;
import com.cocinadelicia.backend.product.repository.ModifierOptionRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AdminModifierGroupServiceImplTest {

  private ModifierGroupRepository groupRepository;
  private ModifierOptionRepository optionRepository;
  private ProductVariantRepository variantRepository;
  private ProductRepository productRepository;
  private AdminModifierGroupServiceImpl service;

  @BeforeEach
  void setUp() {
    groupRepository = mock(ModifierGroupRepository.class);
    optionRepository = mock(ModifierOptionRepository.class);
    variantRepository = mock(ProductVariantRepository.class);
    productRepository = mock(ProductRepository.class);
    service =
        new AdminModifierGroupServiceImpl(
            groupRepository, optionRepository, variantRepository, productRepository);
  }

  @Test
  void create_withMinGreaterThanMax_shouldThrowBadRequest() {
    ModifierGroupAdminRequest request =
        new ModifierGroupAdminRequest(1L, "Guarniciones", 3, 1, "MULTI", null, null, 0, true);

    assertThrows(BadRequestException.class, () -> service.create(request));

    Mockito.verifyNoInteractions(
        variantRepository, groupRepository, optionRepository, productRepository);
  }
}
