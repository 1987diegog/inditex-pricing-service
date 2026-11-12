package com.inditex.pricing.application.model;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ApplicablePriceQuery(
        LocalDateTime applicationDate,
        Long productId,
        Long brandId) {

    public ApplicablePriceQuery {
        if (applicationDate == null || productId == null || brandId == null) {
            throw new IllegalArgumentException("Price query parameters must not be null");
        }
        if (productId <= 0 || brandId <= 0) {
            throw new IllegalArgumentException("Product and brand identifiers must be positive");
        }
    }
}
