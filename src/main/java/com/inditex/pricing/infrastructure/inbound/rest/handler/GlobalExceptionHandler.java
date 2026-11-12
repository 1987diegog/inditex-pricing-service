package com.inditex.pricing.infrastructure.inbound.rest.handler;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.inditex.pricing.application.exception.ApplicablePriceNotFoundException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String APPLICABLE_PRICE_NOT_FOUND_TITLE = "Applicable price not found";
    private static final String INVALID_REQUEST_PARAMETERS_TITLE = "Invalid request parameters";
    private static final String INTERNAL_SERVER_ERROR_TITLE = "Internal server error";
    public static final String AN_UNEXPECTED_ERROR_OCCURRED_DETAIL = "An unexpected error occurred";
    public static final String REQUEST_PARAMETERS_ARE_INVALID_DETAIL = "One or more request parameters are invalid";

    @ExceptionHandler(ApplicablePriceNotFoundException.class)
    ResponseEntity<ProblemDetail> handleApplicablePriceNotFound(ApplicablePriceNotFoundException exception,
                                                                HttpServletRequest request) {

        log.info("Applicable price not found: path={}, message={}",
                request.getRequestURI(), exception.getMessage());

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());

        problem.setTitle(APPLICABLE_PRICE_NOT_FOUND_TITLE);
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {

        log.warn("Request constraint violation: path={}, violations={}",
                request.getRequestURI(), exception.getConstraintViolations());

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, REQUEST_PARAMETERS_ARE_INVALID_DETAIL);

        problem.setTitle(INVALID_REQUEST_PARAMETERS_TITLE);
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> handleUnexpectedException(Exception exception, HttpServletRequest request) {

        log.error("Unexpected error processing path={}",
                request.getRequestURI(), exception);

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                AN_UNEXPECTED_ERROR_OCCURRED_DETAIL);

        problem.setTitle(INTERNAL_SERVER_ERROR_TITLE);
        problem.setInstance(URI.create(request.getRequestURI()));

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problem);
    }


    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, @Nullable Object body,
                                                             HttpHeaders headers, HttpStatusCode status,
                                                             WebRequest request) {

        var servletRequest = ((ServletWebRequest) request).getRequest();

        log.warn("Request rejected: status={}, path={}, reason={}",
                status.value(), servletRequest.getRequestURI(), exception.getMessage());

        return super.handleExceptionInternal(exception, body, headers, status, request);
    }
}
