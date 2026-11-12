package com.inditex.pricing.infrastructure.inbound.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import com.inditex.pricing.application.model.ApplicablePriceQuery;
import com.inditex.pricing.application.port.inbound.FindApplicablePriceInboundPort;
import com.inditex.pricing.domain.model.ApplicablePrice;
import com.inditex.pricing.generated.api.PricesApi;
import com.inditex.pricing.generated.model.ApplicablePriceResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PricesController implements PricesApi {

    private final FindApplicablePriceInboundPort findApplicablePriceInboundPort;
    private final ApplicationDateParser applicationDateParser;
    private final PricesApiMapper mapper;

    @Override
    public ResponseEntity<ApplicablePriceResponse> getApplicablePrice(String applicationDate,
                                                                      Long productId, Long brandId) {

        log.debug("Resolving applicable price: productId={}, brandId={}, applicationDate={}",
                productId, brandId, applicationDate);

        var query = ApplicablePriceQuery.builder()
                .applicationDate(applicationDateParser.parse(applicationDate))
                .productId(productId).brandId(brandId)
                .build();
        ApplicablePrice applicablePrice = findApplicablePriceInboundPort.findApplicablePrice(query);
        ApplicablePriceResponse response = mapper.toResponse(applicablePrice);

        log.debug("Applicable price resolved: productId={}, brandId={}, priceList={}",
                applicablePrice.productId(), applicablePrice.brandId(), applicablePrice.priceList());

        return ResponseEntity.ok(response);
    }
}
