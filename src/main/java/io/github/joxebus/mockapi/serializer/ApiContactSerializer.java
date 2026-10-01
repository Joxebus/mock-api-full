package io.github.joxebus.mockapi.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.ApiContact;

public class ApiContactSerializer extends StdSerializer<ApiContact> {

    public ApiContactSerializer(Class<ApiContact> type) {
        super(type);
    }

    @Override
    public void serialize(ApiContact apiContact, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        genApiContact(apiContact, gen);
        gen.writeEndObject();
    }

    public void genApiContact(ApiContact apiContact, JsonGenerator gen) throws IOException {
        gen.writeStringField("name", apiContact.getName());
        gen.writeStringField("url", apiContact.getUrl());
        gen.writeStringField("email", apiContact.getEmail());
    }

}
