package com.halasa.demoshop.rest.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.halasa.demoshop.rest.ErrorCode;

import java.io.IOException;

public class ErrorCodeJsonSerializer extends JsonSerializer<ErrorCode> {

    @Override
    public void serialize(ErrorCode errorCode, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(errorCode.number);
    }
}
