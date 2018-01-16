package com.halasa.demoshop.service.validation;

import com.halasa.demoshop.rest.ErrorCode;

public class UnsupportedAssociationFetchException extends ValidationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.UNSUPPORTED_ASSOCIATION_FETCH;

    public UnsupportedAssociationFetchException(String message) {
        super(ERROR_CODE, message);
    }

    public UnsupportedAssociationFetchException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
