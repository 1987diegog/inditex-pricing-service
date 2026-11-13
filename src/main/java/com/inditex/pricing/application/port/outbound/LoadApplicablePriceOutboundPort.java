package com.inditex.pricing.application.port.outbound;

import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.domain.model.ApplicablePrice;
import java.util.Optional;

public interface LoadApplicablePriceOutboundPort {
    Optional<ApplicablePrice> loadApplicablePrice(ApplicablePriceQuery query);
}
