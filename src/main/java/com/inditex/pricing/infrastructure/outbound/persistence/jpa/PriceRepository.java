package com.inditex.pricing.infrastructure.outbound.persistence.jpa;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface PriceRepository extends JpaRepository<PriceEntity, Long> {

    List<PriceEntity>
    findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long brandId,
            Long productId,
            LocalDateTime applicationDateForStart,
            LocalDateTime applicationDateForEnd);
}
