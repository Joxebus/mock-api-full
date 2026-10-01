package io.github.joxebus.mockapi.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.ApiLicense;

public class ApiLicenseSerializer extends StdSerializer<ApiLicense> {

    public ApiLicenseSerializer(Class<ApiLicense> type) {
        super(type);
    }

    @Override
    public void serialize(ApiLicense apiLicense, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        genApiLicense(apiLicense, gen);
        gen.writeEndObject();
    }

    public void genApiLicense(ApiLicense apiLicense, JsonGenerator gen) throws IOException {
        gen.writeStringField("name", apiLicense.getName());
        gen.writeStringField("url", apiLicense.getUrl());
    }

}
