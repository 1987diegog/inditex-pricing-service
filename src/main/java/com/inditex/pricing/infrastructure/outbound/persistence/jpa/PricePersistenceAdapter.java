package com.inditex.pricing.infrastructure.outbound.persistence.jpa;

import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.application.port.outbound.LoadApplicablePriceOutboundPort;
import com.inditex.pricing.domain.model.ApplicablePrice;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
class PricePersistenceAdapter implements LoadApplicablePriceOutboundPort {

    private final PriceRepository repository;
    private final PriceMapper mapper;

    @Override
    public Optional<ApplicablePrice> loadApplicablePrice(ApplicablePriceQuery query) {

        log.debug("Loading applicable price from persistence: "
                + "productId={}, brandId={}, applicationDate={}",
                query.productId(), query.brandId(), query.applicationDate());

        return repository
                .findFirstByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByPriorityDesc(
                        query.brandId(),
                        query.productId(),
                        query.applicationDate(),
                        query.applicationDate())
                .map(mapper::toDomain);
    }
}
