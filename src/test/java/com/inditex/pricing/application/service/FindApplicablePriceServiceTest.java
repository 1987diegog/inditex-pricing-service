package com.inditex.pricing.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.inditex.pricing.application.exception.ApplicablePriceNotFoundException;
import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.application.port.outbound.LoadApplicablePriceOutboundPort;
import com.inditex.pricing.domain.model.ApplicablePrice;
import static com.inditex.pricing.PriceTestData.BRAND_ID;
import static com.inditex.pricing.PriceTestData.PRODUCT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
    void shouldReturnApplicablePriceWithHighestPriority() {
        var query = ApplicablePriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 16, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        var lowerPriorityPrice = buildApplicablePrice(10, 0);
        var higherPriorityPrice = buildApplicablePrice(20, 1);

        when(priceLoader.loadApplicablePrice(query))
                .thenReturn(List.of(
                        lowerPriorityPrice,
                        higherPriorityPrice));

        var result = service.findApplicablePrice(query);

        assertThat(result).isEqualTo(higherPriorityPrice);
    }


    @ParameterizedTest(name = "Candidate order: {0}, {1}, {2}")
    @MethodSource("candidateOrders")
    void shouldReturnHighestNumericPriorityRegardlessOfCandidateOrder(int firstIndex, int secondIndex, int thirdIndex) {
        var query = ApplicablePriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 16, 0))
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .build();

        var lowestPriorityPrice = buildApplicablePrice(10, 1);
        var intermediatePriorityPrice = buildApplicablePrice(30, 2);
        var highestPriorityPrice = buildApplicablePrice(20, 5);

        var candidates = List.of(lowestPriorityPrice, intermediatePriorityPrice, highestPriorityPrice);

        when(priceLoader.loadApplicablePrice(query))
                .thenReturn(List.of(
                        candidates.get(firstIndex),
                        candidates.get(secondIndex),
                        candidates.get(thirdIndex)));

        var result = service.findApplicablePrice(query);

        assertThat(result).isEqualTo(highestPriorityPrice);
    }

    private static Stream<Arguments> candidateOrders() {
        return Stream.of(
                Arguments.of(0, 1, 2),
                Arguments.of(0, 2, 1),
                Arguments.of(1, 0, 2),
                Arguments.of(1, 2, 0),
                Arguments.of(2, 0, 1),
                Arguments.of(2, 1, 0)
        );
    }

    @Test
    void shouldThrowExceptionWhenNoApplicablePriceExists() {
        var query = ApplicablePriceQuery.builder()
                .applicationDate(LocalDateTime.of(2020, 6, 14, 16, 0))
                .productId(35455L)
                .brandId(1L)
                .build();

        when(priceLoader.loadApplicablePrice(query))
                .thenReturn(List.of());

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
