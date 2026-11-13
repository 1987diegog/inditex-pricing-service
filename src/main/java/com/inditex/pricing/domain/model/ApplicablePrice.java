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

    public ApplicablePrice {
        if (productId == null || brandId == null || priceList == null
                || startDate == null || endDate == null
                || priority == null || price == null || currency == null) {
            throw new IllegalArgumentException("Price fields must not be null");
        }
        if (productId <= 0 || brandId <= 0 || priceList <= 0) {
            throw new IllegalArgumentException(
                    "Product, brand and price-list identifiers must be positive");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        if (priority < 0) {
            throw new IllegalArgumentException("Priority must not be negative");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must not precede start date");
        }
    }
}
