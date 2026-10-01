package io.github.joxebus.mockapi.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiPath;
import io.github.joxebus.mockapi.model.Endpoint;
import io.github.joxebus.mockapi.model.EndpointConfiguration;
import io.github.joxebus.mockapi.model.ResponseError;
import io.github.joxebus.mockapi.serializer.ApiConfigurationSerializer;
import io.github.joxebus.mockapi.serializer.ApiPathSerializer;
import io.github.joxebus.mockapi.serializer.EndpointConfigurationSerializer;
import io.github.joxebus.mockapi.serializer.EndpointSerializer;
import io.github.joxebus.mockapi.serializer.ResponseErrorSerializer;

@Configuration
public class Config {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String[] corsAllowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/config/**")
                        .allowedOrigins(corsAllowedOrigins)
                        .allowedMethods("GET", "POST", "DELETE", "OPTIONS");
                registry.addMapping("/endpoint/**")
                        .allowedOrigins(corsAllowedOrigins)
                        .allowedMethods("GET", "OPTIONS");
            }
        };
    }

    @Bean
    public ObjectMapper jsonMapper(){
        ObjectMapper customObjectMapper = new ObjectMapper();
        customObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        customObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        SimpleModule customSerializersModule = new SimpleModule();
        customSerializersModule.addSerializer(Endpoint.class, new EndpointSerializer(Endpoint.class));
        customSerializersModule.addSerializer(EndpointConfiguration.class, new EndpointConfigurationSerializer(EndpointConfiguration.class));
        customSerializersModule.addSerializer(ApiPath.class, new ApiPathSerializer(ApiPath.class));
        customSerializersModule.addSerializer(ApiConfiguration.class, new ApiConfigurationSerializer(ApiConfiguration.class));
        customSerializersModule.addSerializer(ResponseError.class, new ResponseErrorSerializer(ResponseError.class));

        customObjectMapper.registerModule(customSerializersModule);

        // additional indentation for arrays
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(new DefaultIndenter());
        customObjectMapper.setDefaultPrettyPrinter(pp);

        return customObjectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper mapper) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(mapper);
        return converter;
    }

    
}
