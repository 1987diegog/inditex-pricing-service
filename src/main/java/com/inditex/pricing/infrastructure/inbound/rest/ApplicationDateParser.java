package com.inditex.pricing.infrastructure.inbound.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ApplicationDateParser {

    private static final String DATE_PATTERN =
            "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}";

    public LocalDateTime parse(String value) {
        if (value == null || !value.matches(DATE_PATTERN)) {
            throw invalidDate();
        }

        try {
            return LocalDateTime.parse(value, ApiDateTimeFormat.FORMATTER);
        } catch (DateTimeParseException exception) {
            throw invalidDate();
        }
    }

    private ResponseStatusException invalidDate() {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "applicationDate must be a valid local date-time "
                        + "in yyyy-MM-dd'T'HH:mm:ss format, without time zone or offset");
    }
}
