package com.inditex.pricing.application.port.inbound;

import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.domain.model.ApplicablePrice;

public interface FindApplicablePriceInboundPort {
    ApplicablePrice findApplicablePrice(ApplicablePriceQuery query);
}
