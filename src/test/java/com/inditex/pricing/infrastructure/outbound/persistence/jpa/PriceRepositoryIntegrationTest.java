package com.inditex.pricing.infrastructure.outbound.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static com.inditex.pricing.PriceTestData.BRAND_ID;
import static com.inditex.pricing.PriceTestData.PRODUCT_ID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PriceRepositoryIntegrationTest {

    @Autowired
    private PriceRepository repository;

    @Test
    void shouldReturnAllApplicablePricesWhenDateRangesOverlap() {
        var result = findApplicablePricesAt("2020-06-14T16:00:00");

        assertThat(result)
                .extracting(PriceEntity::getPriceList)
                .containsExactlyInAnyOrder(1, 2);
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
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        BRAND_ID,
                        99999L,
                        date,
                        date);

        assertThat(result).isEmpty();
    }

    private List<PriceEntity> findApplicablePricesAt(String applicationDate) {

        var date = LocalDateTime.parse(applicationDate);

        return repository
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        BRAND_ID,
                        PRODUCT_ID,
                        date,
                        date);
    }
}
