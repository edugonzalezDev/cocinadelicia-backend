package com.cocinadelicia.backend.catalog.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.cocinadelicia.backend.catalog.admin.service.impl.AdminProductVariantServiceImpl;
import com.cocinadelicia.backend.common.exception.NotFoundException;
import com.cocinadelicia.backend.common.model.enums.CurrencyCode;
import com.cocinadelicia.backend.product.dto.ProductVariantAdminResponse;
import com.cocinadelicia.backend.product.dto.ProductVariantPriceUpdateRequest;
import com.cocinadelicia.backend.product.mapper.ProductVariantMapper;
import com.cocinadelicia.backend.product.model.PriceHistory;
import com.cocinadelicia.backend.product.model.ProductVariant;
import com.cocinadelicia.backend.product.repository.PriceHistoryRepository;
import com.cocinadelicia.backend.product.repository.ProductRepository;
import com.cocinadelicia.backend.product.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminProductVariantServiceImplTest {

  @Mock ProductVariantRepository variantRepository;
  @Mock ProductRepository productRepository;
  @Mock PriceHistoryRepository priceHistoryRepository;
  @Mock ProductVariantMapper mapper;

  @InjectMocks AdminProductVariantServiceImpl service;

  private ProductVariant variant;

  @BeforeEach
  void setup() {
    variant = new ProductVariant();
    variant.setId(10L);
    org.mockito.Mockito.lenient()
        .when(mapper.toAdminResponse(any()))
        .thenReturn(new ProductVariantAdminResponse(10L, "Var", "SKU", true, false, 0));
  }

  @Test
  void updateActivePrice_withPrevious_closesAndCreates() {
    PriceHistory active =
        PriceHistory.builder()
            .id(1L)
            .productVariant(variant)
            .price(new BigDecimal("100.00"))
            .currency(CurrencyCode.UYU)
            .validFrom(Instant.parse("2025-01-01T10:00:00Z"))
            .validTo(null)
            .build();

    when(variantRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(variant));
    when(priceHistoryRepository.findActivePriceEntryForUpdate(10L)).thenReturn(Optional.of(active));
    when(priceHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    service.updateActivePrice(
        10L, new ProductVariantPriceUpdateRequest(new BigDecimal("200.00"), CurrencyCode.USD));

    assertThat(active.getValidTo()).isNotNull();

    ArgumentCaptor<PriceHistory> captor = ArgumentCaptor.forClass(PriceHistory.class);
    verify(priceHistoryRepository, times(2)).save(captor.capture());

    List<PriceHistory> saved = captor.getAllValues();
    PriceHistory newEntry =
        saved.stream().filter(entry -> entry.getValidTo() == null).findFirst().orElseThrow();

    assertThat(newEntry.getPrice()).isEqualByComparingTo("200.00");
    assertThat(newEntry.getCurrency()).isEqualTo(CurrencyCode.USD);
    assertThat(newEntry.getValidFrom()).isNotNull();
    assertThat(newEntry.getProductVariant()).isEqualTo(variant);
  }

  @Test
  void updateActivePrice_withoutPrevious_createsSingleActive() {
    when(variantRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(variant));
    when(priceHistoryRepository.findActivePriceEntryForUpdate(10L)).thenReturn(Optional.empty());
    when(priceHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    service.updateActivePrice(
        10L, new ProductVariantPriceUpdateRequest(new BigDecimal("120.00"), null));

    ArgumentCaptor<PriceHistory> captor = ArgumentCaptor.forClass(PriceHistory.class);
    verify(priceHistoryRepository, times(1)).save(captor.capture());

    PriceHistory newEntry = captor.getValue();
    assertThat(newEntry.getValidTo()).isNull();
    assertThat(newEntry.getCurrency()).isEqualTo(CurrencyCode.UYU);
    assertThat(newEntry.getPrice()).isEqualByComparingTo("120.00");
  }

  @Test
  void updateActivePrice_twoUpdates_lastIsActive() {
    AtomicReference<PriceHistory> activeRef = new AtomicReference<>();
    List<PriceHistory> savedEntries = new ArrayList<>();

    when(variantRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(variant));
    when(priceHistoryRepository.findActivePriceEntryForUpdate(10L))
        .thenAnswer(inv -> Optional.ofNullable(activeRef.get()));
    when(priceHistoryRepository.save(any()))
        .thenAnswer(
            inv -> {
              PriceHistory entry = inv.getArgument(0);
              savedEntries.add(entry);
              if (entry.getValidTo() == null) {
                activeRef.set(entry);
              }
              return entry;
            });

    service.updateActivePrice(
        10L, new ProductVariantPriceUpdateRequest(new BigDecimal("100.00"), null));
    service.updateActivePrice(
        10L, new ProductVariantPriceUpdateRequest(new BigDecimal("150.00"), null));

    PriceHistory active = activeRef.get();
    assertThat(active).isNotNull();
    assertThat(active.getPrice()).isEqualByComparingTo("150.00");
    assertThat(active.getValidTo()).isNull();

    boolean previousClosed = savedEntries.stream().anyMatch(entry -> entry.getValidTo() != null);
    assertThat(previousClosed).isTrue();
  }

  @Test
  void updateActivePrice_variantNotFound_throwsNotFound() {
    when(variantRepository.findByIdForUpdate(10L)).thenReturn(Optional.empty());

    assertThatThrownBy(
            () ->
                service.updateActivePrice(
                    10L, new ProductVariantPriceUpdateRequest(new BigDecimal("100.00"), null)))
        .isInstanceOf(NotFoundException.class);
  }
}
