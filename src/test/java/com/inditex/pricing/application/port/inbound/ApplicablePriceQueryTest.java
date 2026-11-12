package com.inditex.pricing.application.port.inbound;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import com.inditex.pricing.application.model.ApplicablePriceQuery;
import static com.inditex.pricing.PriceTestData.BRAND_ID;
import static com.inditex.pricing.PriceTestData.PRODUCT_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplicablePriceQueryTest {

    @Test
    void shouldRejectNonPositiveIdentifiers() {
        assertThatThrownBy(() -> ApplicablePriceQuery.builder()
                .applicationDate(LocalDateTime.now())
                .productId(0L)
                .brandId(BRAND_ID)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullValues() {
        assertThatThrownBy(() -> ApplicablePriceQuery.builder()
                .applicationDate(null)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .build())
                .isInstanceOf(IllegalArgumentException.class);
    }
}
