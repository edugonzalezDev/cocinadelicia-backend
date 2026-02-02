package com.cocinadelicia.backend.catalog.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminPatchRequest;
import com.cocinadelicia.backend.catalog.admin.dto.ProductImageAdminRequest;
import com.cocinadelicia.backend.catalog.admin.service.impl.AdminProductImageServiceImpl;
import com.cocinadelicia.backend.common.exception.BadRequestException;
import com.cocinadelicia.backend.common.s3.CdnUrlBuilder;
import com.cocinadelicia.backend.product.model.Product;
import com.cocinadelicia.backend.product.model.ProductImage;
import com.cocinadelicia.backend.product.repository.ProductImageRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminProductImageServiceImplTest {

  @Mock ProductRepository productRepository;
  @Mock ProductImageRepository productImageRepository;
  @Mock CdnUrlBuilder cdnUrlBuilder;

  @InjectMocks AdminProductImageServiceImpl service;

  private Product product;

  @BeforeEach
  void setup() {
    product = new Product();
    product.setId(10L);
    when(cdnUrlBuilder.toPublicUrl(any())).thenReturn("http://cdn/url");
  }

  @Test
  void addToProduct_noMain_setsMainAndClampsSortOrder() {
    when(productRepository.findById(10L)).thenReturn(Optional.of(product));
    when(productImageRepository.existsByProduct_IdAndIsMainTrue(10L)).thenReturn(false);
    when(productImageRepository.findByProduct_IdOrderBySortOrderAscCreatedAtAsc(10L))
        .thenReturn(List.of());
    when(productImageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    var req = new ProductImageAdminRequest("products/10/a.png", false, -3);
    var res = service.addToProduct(10L, req);

    assertThat(res.isMain()).isTrue();
    assertThat(res.sortOrder()).isEqualTo(0);
  }

  @Test
  void addToProduct_invalidObjectKey_throwsBadRequest() {
    when(productRepository.findById(10L)).thenReturn(Optional.of(product));

    var req = new ProductImageAdminRequest("invalid.png", false, 0);

    assertThatThrownBy(() -> service.addToProduct(10L, req))
        .isInstanceOf(BadRequestException.class);
  }

  @Test
  void patch_isMainTrue_swapsMain() {
    ProductImage img1 = ProductImage.builder().id(1L).product(product).isMain(false).build();
    ProductImage img2 = ProductImage.builder().id(2L).product(product).isMain(true).build();

    when(productImageRepository.findById(1L)).thenReturn(Optional.of(img1));
    when(productImageRepository.findByProduct_IdOrderBySortOrderAscCreatedAtAsc(10L))
        .thenReturn(List.of(img1, img2));

    service.patch(1L, new ProductImageAdminPatchRequest(true, null));

    assertThat(img1.isMain()).isTrue();
    assertThat(img2.isMain()).isFalse();
    verify(productImageRepository).saveAll(any());
  }

  @Test
  void patch_isMainFalse_onMain_reassigns() {
    ProductImage img1 = ProductImage.builder().id(1L).product(product).isMain(true).build();
    ProductImage img2 = ProductImage.builder().id(2L).product(product).isMain(false).build();

    when(productImageRepository.findById(1L)).thenReturn(Optional.of(img1));
    when(productImageRepository.findByProduct_IdOrderBySortOrderAscCreatedAtAsc(10L))
        .thenReturn(List.of(img1, img2));

    service.patch(1L, new ProductImageAdminPatchRequest(false, null));

    assertThat(img1.isMain()).isFalse();
    assertThat(img2.isMain()).isTrue();
    verify(productImageRepository).saveAll(any());
  }

  @Test
  void delete_main_reassignsNext() {
    ProductImage main = ProductImage.builder().id(1L).product(product).isMain(true).build();
    ProductImage img2 = ProductImage.builder().id(2L).product(product).isMain(false).build();
    ProductImage img3 = ProductImage.builder().id(3L).product(product).isMain(false).build();

    when(productImageRepository.findById(1L)).thenReturn(Optional.of(main));
    when(productImageRepository.findByProduct_IdOrderBySortOrderAscCreatedAtAsc(10L))
        .thenReturn(List.of(img2, img3));

    service.delete(1L);

    assertThat(img2.isMain()).isTrue();
    assertThat(img3.isMain()).isFalse();
    verify(productImageRepository).saveAll(any());
  }
}
