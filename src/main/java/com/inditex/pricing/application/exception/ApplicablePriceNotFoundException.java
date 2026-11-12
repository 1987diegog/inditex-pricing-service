package com.inditex.pricing.application.exception;

import java.time.LocalDateTime;

public class ApplicablePriceNotFoundException extends RuntimeException {
    public ApplicablePriceNotFoundException(LocalDateTime date, Long productId, Long brandId) {
        super("No applicable price found for product %d, brand %d and date %s"
                .formatted(productId, brandId, date));
    }
}
