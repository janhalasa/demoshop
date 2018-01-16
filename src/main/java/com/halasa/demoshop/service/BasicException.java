package com.halasa.demoshop.service;

import com.halasa.demoshop.rest.ErrorCode;

public class BasicException extends RuntimeException {

    private final ErrorCode errorCode;

    public BasicException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BasicException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
