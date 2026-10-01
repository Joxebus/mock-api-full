package io.github.joxebus.mockapi.serializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiContact;
import io.github.joxebus.mockapi.model.ApiLicense;
import io.github.joxebus.mockapi.model.ApiPath;

public class ApiConfigurationSerializer extends StdSerializer<ApiConfiguration> {

    private final ApiPathSerializer apiOperationSerializer;
    private final ApiContactSerializer apiContactSerializer;
    private final ApiLicenseSerializer apiLicenseSerializer;

    public ApiConfigurationSerializer(Class<ApiConfiguration> type) {
        super(type);
        this.apiOperationSerializer = new ApiPathSerializer(ApiPath.class);
        this.apiContactSerializer = new ApiContactSerializer(ApiContact.class);
        this.apiLicenseSerializer = new ApiLicenseSerializer(ApiLicense.class);
    }

    @Override
    public void serialize(ApiConfiguration apiConfiguration, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        genApiConfiguration(apiConfiguration, gen);
        gen.writeEndObject();
    }

    private void genApiConfiguration(ApiConfiguration apiConfiguration, JsonGenerator gen) throws IOException {
        gen.writeStringField("name", apiConfiguration.getName());
        gen.writeStringField("description", apiConfiguration.getDescription());
        gen.writeStringField("termsOfService", apiConfiguration.getTermsOfService());
        gen.writeStringField("version", apiConfiguration.getVersion());
        if(apiConfiguration.getContact() != null) {
            gen.writeObjectFieldStart("contact");
            apiContactSerializer.genApiContact(apiConfiguration.getContact(), gen);
            gen.writeEndObject();
        }
        if(apiConfiguration.getLicense() != null) {
            gen.writeObjectFieldStart("license");
            apiLicenseSerializer.genApiLicense(apiConfiguration.getLicense(), gen);
            gen.writeEndObject();
        }
        gen.writeBooleanField("secured", apiConfiguration.isSecured());
        gen.writeStringField("authConfig", apiConfiguration.getAuthConfig());
        gen.writeObjectFieldStart("paths");
        for(Map.Entry<String, List<ApiPath>> apiOperationEntry : apiConfiguration.getPaths().entrySet()) {
            gen.writeArrayFieldStart(apiOperationEntry.getKey());
            for(ApiPath apiPath: apiOperationEntry.getValue()) {
                gen.writeObject(apiPath);
            }
            gen.writeEndArray();
        }
        gen.writeEndObject();
    }
}
