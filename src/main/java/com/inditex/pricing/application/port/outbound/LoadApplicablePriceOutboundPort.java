package com.inditex.pricing.application.port.outbound;

import java.util.List;
import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.domain.model.ApplicablePrice;

public interface LoadApplicablePriceOutboundPort {
    List<ApplicablePrice> loadApplicablePrice(ApplicablePriceQuery query);
}
