package com.inditex.pricing.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import lombok.Builder;

@Builder
public record ApplicablePrice(
        Long productId,
        Long brandId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer priority,
        BigDecimal price,
        Currency currency) {
}
