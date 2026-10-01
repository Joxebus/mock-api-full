package io.github.joxebus.mockapi.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.Endpoint;
import io.github.joxebus.mockapi.model.EndpointConfiguration;

public class EndpointConfigurationSerializer extends StdSerializer<EndpointConfiguration> {

    public EndpointConfigurationSerializer(Class<EndpointConfiguration> type) {
        super(type);
    }

    @Override
    public void serialize(EndpointConfiguration endpointConfiguration, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        genEndpointConfiguration(endpointConfiguration, gen);
        gen.writeEndObject();
    }

    private void genEndpointConfiguration(EndpointConfiguration endpointConfiguration, JsonGenerator gen) throws IOException {
        gen.writeStringField("config", endpointConfiguration.getConfig());
        gen.writeArrayFieldStart("endpoints");
        for(Endpoint endpoint: endpointConfiguration.getEndpoints()) {
            gen.writeObject(endpoint);
        }
        gen.writeEndArray();
    }
}
