package com.halasa.demoshop.rest;

import com.halasa.demoshop.api.dto.response.ErrorResponse;
import com.halasa.demoshop.service.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(RuntimeException ex) {
        LOGGER.error("Runtime exception", ex);
        return new ErrorResponse(ErrorCode.SYSTEM_FAILURE, "System failure");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();

        final String message = error.getObjectName() + "." + error.getField() + " (" + error.getRejectedValue() + ") "
                + error.getDefaultMessage();
        LOGGER.warn("Validation error: {}", message);

        return new ErrorResponse(ErrorCode.VALIDATION_FAILURE, message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        LOGGER.error("Access denied", ex);
        return new ErrorResponse(
                ErrorCode.ACCESS_DENIED,
                "You are not allowed to access the requested resource or service.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        LOGGER.error("DB integrity violation", ex);
        return new ErrorResponse(
                ErrorCode.DATA_INTEGRITY_VIOLATION,
                "The submitted data causes integrity problems. Some values are probably not unique.");
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        LOGGER.error("Required entity not found in DB", ex);
        return new ErrorResponse(
                ErrorCode.REQUIRED_RESULT_NOT_FOUND,
                "The submitted data causes integrity problems. Some values are probably not unique.");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException ex) {
        LOGGER.info("Invalid input.", ex);
        return new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage());
    }

}
