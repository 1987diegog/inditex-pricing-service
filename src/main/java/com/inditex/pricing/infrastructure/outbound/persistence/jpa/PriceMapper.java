package com.inditex.pricing.infrastructure.outbound.persistence.jpa;

import com.inditex.pricing.domain.model.ApplicablePrice;
import java.util.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface PriceMapper {

    ApplicablePrice toDomain(PriceEntity entity);

    default Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
