package com.inditex.pricing.infrastructure.outbound.persistence.jpa;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.application.port.outbound.LoadApplicablePriceOutboundPort;
import com.inditex.pricing.domain.model.ApplicablePrice;

@Slf4j
@RequiredArgsConstructor
@Component
class PricePersistenceAdapter implements LoadApplicablePriceOutboundPort {

    private final PriceRepository repository;
    private final PriceMapper mapper;

    @Override
    public List<ApplicablePrice> loadApplicablePrice(ApplicablePriceQuery query) {

        log.debug("Loading applicable prices from persistence: productId={}, brandId={}, applicationDate={}",
                query.productId(), query.brandId(), query.applicationDate());

        return repository
                .findByBrandIdAndProductIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        query.brandId(),
                        query.productId(),
                        query.applicationDate(),
                        query.applicationDate())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
