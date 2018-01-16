package com.halasa.demoshop.rest.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.halasa.demoshop.rest.ErrorCode;

import java.io.IOException;

public class ErrorCodeJsonDeserializer extends JsonDeserializer<ErrorCode> {

    @Override
    public ErrorCode deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        int errorCodeNumber = jsonParser.getIntValue();
        return ErrorCode.byNumber(errorCodeNumber);
    }
}
