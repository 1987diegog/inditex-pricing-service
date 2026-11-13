package com.inditex.pricing.application.service;

import static com.inditex.pricing.PriceTestData.BRAND_ID;
import static com.inditex.pricing.PriceTestData.PRODUCT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.inditex.pricing.application.exception.ApplicablePriceNotFoundException;
import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.application.port.outbound.LoadApplicablePriceOutboundPort;
import com.inditex.pricing.domain.model.ApplicablePrice;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindApplicablePriceServiceTest {

    @Mock
    private LoadApplicablePriceOutboundPort priceLoader;

    private FindApplicablePriceService service;

    @BeforeEach
    void setUp() {
        service = new FindApplicablePriceService(priceLoader);
    }

    @Test
    void shouldReturnPriceLoadedFromPersistence() {
        var query = ApplicablePriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 16, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        var higherPriorityPrice = buildApplicablePrice(20, 1);

        when(priceLoader.loadApplicablePrice(query))
                .thenReturn(Optional.of(higherPriorityPrice));

        var result = service.findApplicablePrice(query);

        assertThat(result).isEqualTo(higherPriorityPrice);
    }


    @Test
    void shouldThrowExceptionWhenNoApplicablePriceExists() {
        var query = ApplicablePriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 16, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        when(priceLoader.loadApplicablePrice(query))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findApplicablePrice(query))
                .isInstanceOf(ApplicablePriceNotFoundException.class);
    }

    private ApplicablePrice buildApplicablePrice(Integer priceList, Integer priority) {
        return ApplicablePrice.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(priceList)
                .startDate(LocalDateTime.parse("2020-06-14T15:00:00"))
                .endDate(LocalDateTime.parse("2020-06-14T18:30:00"))
                .priority(priority)
                .price(new BigDecimal("25.45"))
                .currency(Currency.getInstance("EUR"))
                .build();
    }
}
