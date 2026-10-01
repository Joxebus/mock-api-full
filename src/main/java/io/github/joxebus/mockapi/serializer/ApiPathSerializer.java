package io.github.joxebus.mockapi.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.ApiPath;

public class ApiPathSerializer extends StdSerializer<ApiPath> {

    public ApiPathSerializer(Class<ApiPath> type) {
        super(type);
    }

    @Override
    public void serialize(ApiPath apiPath, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        genApiPath(apiPath, gen);
        gen.writeEndObject();
    }

    public void genApiPath(ApiPath apiPath, JsonGenerator gen) throws IOException {
        gen.writeStringField("method", apiPath.getMethod());
        gen.writeStringField("body", apiPath.getBody());
        gen.writeNumberField("statusCode", apiPath.getStatusCode());
        gen.writeObjectField("headers", apiPath.getHeaders());
    }

}
