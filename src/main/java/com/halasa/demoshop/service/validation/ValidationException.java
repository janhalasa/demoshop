package com.halasa.demoshop.service.validation;

import com.halasa.demoshop.rest.ErrorCode;
import com.halasa.demoshop.service.BasicException;

public class ValidationException extends BasicException {

    public ValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ValidationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
