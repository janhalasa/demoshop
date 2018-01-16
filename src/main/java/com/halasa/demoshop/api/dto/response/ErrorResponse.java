package com.halasa.demoshop.api.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.halasa.demoshop.rest.ErrorCode;
import com.halasa.demoshop.rest.converter.ErrorCodeJsonDeserializer;
import com.halasa.demoshop.rest.converter.ErrorCodeJsonSerializer;

public class ErrorResponse {

    @JsonSerialize(using = ErrorCodeJsonSerializer.class)
    @JsonDeserialize(using = ErrorCodeJsonDeserializer.class)
    private ErrorCode code;

    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(ErrorCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
