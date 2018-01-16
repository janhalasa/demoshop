package com.halasa.demoshop.rest;

import java.util.Arrays;

public enum ErrorCode {

    SYSTEM_FAILURE(1000),

    ACCESS_DENIED(2000),

    VALIDATION_FAILURE(3000),
    INVALID_REFERENCE_CODE(3001),
    ORDER_BY_INVALID_FORMAT(3002),

    DATA_INTEGRITY_VIOLATION(4000),
    REQUIRED_RESULT_NOT_FOUND(4001),
    UNSUPPORTED_ASSOCIATION_FETCH(4002);

    public final int number;

    ErrorCode(int number) {
        this.number = number;
    }

    public static ErrorCode byNumber(int code) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorCode -> errorCode.number == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No error for number " + code));
    }
}
