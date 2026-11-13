package com.inditex.pricing.application.service;

import com.inditex.pricing.application.exception.ApplicablePriceNotFoundException;
import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.application.port.inbound.FindApplicablePriceInboundPort;
import com.inditex.pricing.application.port.outbound.LoadApplicablePriceOutboundPort;
import com.inditex.pricing.domain.model.ApplicablePrice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindApplicablePriceService implements FindApplicablePriceInboundPort {

    private final LoadApplicablePriceOutboundPort priceLoader;

    @Override
    public ApplicablePrice findApplicablePrice(ApplicablePriceQuery query) {
        return priceLoader.loadApplicablePrice(query)
                .orElseThrow(() -> new ApplicablePriceNotFoundException(
                        query.applicationDate(),
                        query.productId(),
                        query.brandId()));
    }
}
