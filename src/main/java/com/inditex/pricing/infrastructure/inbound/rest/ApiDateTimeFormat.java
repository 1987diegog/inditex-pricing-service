package com.inditex.pricing.infrastructure.inbound.rest;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ApiDateTimeFormat {

    static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss")
                    .withResolverStyle(ResolverStyle.STRICT);
}
