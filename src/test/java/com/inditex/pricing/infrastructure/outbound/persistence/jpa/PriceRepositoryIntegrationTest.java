package com.inditex.pricing.infrastructure.outbound.persistence.jpa;

import static com.inditex.pricing.PriceTestData.BRAND_ID;
import static com.inditex.pricing.PriceTestData.PRODUCT_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class PriceRepositoryIntegrationTest {

    @Autowired
    private PriceRepository repository;

    @Test
    void shouldReturnHighestPriorityPriceWhenDateRangesOverlap() {
        var result = findApplicablePricesAt("2020-06-14T16:00:00");

        assertThat(result)
                .map(PriceEntity::getPriceList)
                .contains(2);
    }

    @ParameterizedTest
    @CsvSource({"1,2,5", "1,5,2", "2,1,5", "2,5,1", "5,1,2", "5,2,1"})
    void shouldSelectHighestNumericPriorityRegardlessOfInsertionOrder(
            int first,
            int second,
            int third) {
        for (int priority : new int[]{first, second, third}) {
            repository.saveAndFlush(PriceEntity.builder()
                    .brandId(BRAND_ID)
                    .productId(PRODUCT_ID)
                    .priceList(10 + priority)
                    .priority(priority)
                    .startDate(LocalDateTime.parse("2020-06-14T00:00:00"))
                    .endDate(LocalDateTime.parse("2020-12-31T23:59:59"))
                    .price(new BigDecimal("10.00"))
                    .currency("EUR")
                    .build());
        }

        assertThat(findApplicablePricesAt("2020-06-14T16:00:00"))
                .hasValueSatisfying(price -> {
                    assertThat(price.getPriority()).isEqualTo(5);
                    assertThat(price.getPriceList()).isEqualTo(15);
                });
    }

    @Test
    void shouldReturnEmptyWhenNoPriceIsApplicable() {
        var result = findApplicablePricesAt("2021-01-01T00:00:00");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenProductDoesNotExist() {
        var date = LocalDateTime.parse("2020-06-14T16:00:00");

        var result = repository
                .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        BRAND_ID,
                        99999L,
                        date,
                        date);

        assertThat(result).isEmpty();
    }

    private Optional<PriceEntity> findApplicablePricesAt(String applicationDate) {

        var date = LocalDateTime.parse(applicationDate);

        return repository
                .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        BRAND_ID,
                        PRODUCT_ID,
                        date,
                        date);
    }
}
