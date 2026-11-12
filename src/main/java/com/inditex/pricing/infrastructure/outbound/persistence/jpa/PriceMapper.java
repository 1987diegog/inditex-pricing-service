package com.inditex.pricing.infrastructure.outbound.persistence.jpa;

import java.util.Currency;
import org.mapstruct.Mapper;
import com.inditex.pricing.domain.model.ApplicablePrice;

@Mapper(componentModel = "spring")
interface PriceMapper {

    ApplicablePrice toDomain(PriceEntity entity);

    default Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
