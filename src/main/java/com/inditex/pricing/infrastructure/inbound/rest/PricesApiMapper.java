package com.inditex.pricing.infrastructure.inbound.rest;

import com.inditex.pricing.domain.model.ApplicablePrice;
import com.inditex.pricing.generated.model.ApplicablePriceResponse;
import java.time.LocalDateTime;
import java.util.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface PricesApiMapper {

    ApplicablePriceResponse toResponse(ApplicablePrice applicablePrice);

    default String toCurrencyCode(Currency currency) {
        return currency == null
                ? null
                : currency.getCurrencyCode();
    }

    default String toLocalDateTimeString(LocalDateTime value) {
        return value == null
                ? null
                : value.format(ApiDateTimeFormat.FORMATTER);
    }

}
