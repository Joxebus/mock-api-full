package io.github.joxebus.mockapi.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.ResponseError;

public class ResponseErrorSerializer extends StdSerializer<ResponseError> {

    public ResponseErrorSerializer(Class<ResponseError> type) {
        super(type);
    }

    @Override
    public void serialize(ResponseError responseError, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("message", responseError.getMessage());
        gen.writeEndObject();
    }
}
