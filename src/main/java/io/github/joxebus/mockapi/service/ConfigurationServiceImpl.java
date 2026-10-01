package io.github.joxebus.mockapi.service;

import static io.github.joxebus.mockapi.common.Constants.BAD_REQUEST_CODE;
import static io.github.joxebus.mockapi.common.Constants.CREATED_CODE;
import static io.github.joxebus.mockapi.common.Constants.INTERNAL_SERVER_ERROR;
import static io.github.joxebus.mockapi.common.Constants.NOT_FOUND_CODE;
import static io.github.joxebus.mockapi.common.Constants.OK_CODE;
import static io.github.joxebus.mockapi.common.Constants.YAML_EXT;
import static io.github.joxebus.mockapi.utils.MappingUtils.mapApiConfigurationToEndpointConfiguration;
import static io.github.joxebus.mockapi.utils.MappingUtils.mapApiConfigurationToYamlFile;
import static io.github.joxebus.mockapi.utils.MappingUtils.mapYamlFileToApiConfiguration;

import java.io.File;
import java.util.Optional;

import io.github.joxebus.mockapi.utils.ApiConfigurationValidator;

import org.springframework.stereotype.Service;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiResponse;
import io.github.joxebus.mockapi.model.EndpointConfiguration;
import io.github.joxebus.mockapi.model.FileResponse;
import io.github.joxebus.mockapi.model.ResponseError;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {

    private FileService fileService;

    public ConfigurationServiceImpl(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public ApiResponse createOrUpdateConfiguration(ApiConfiguration apiConfiguration) {
        ApiResponse apiResponse = new ApiResponse();
        Optional<String> validationError = ApiConfigurationValidator.validate(apiConfiguration);
        if(validationError.isPresent()) {
            String message = validationError.get();
            log.warn("Invalid configuration: {}", message);
            apiResponse.setStatusCode(BAD_REQUEST_CODE);
            apiResponse.setBody(ResponseError.newError(message));
            return apiResponse;
        }
        File configuration = mapApiConfigurationToYamlFile(apiConfiguration);
        FileResponse fileResponse = fileService.upload(apiConfiguration.getName() + YAML_EXT, configuration);
        if(fileResponse.isSuccess()) {
            EndpointConfiguration endpointConfiguration = mapApiConfigurationToEndpointConfiguration(apiConfiguration);
            apiResponse.setBody(endpointConfiguration);
            apiResponse.setStatusCode(CREATED_CODE);
        } else {
            apiResponse.setStatusCode(INTERNAL_SERVER_ERROR);
            apiResponse.setBody(fileResponse.getError());
        }
        return apiResponse;
    }

    @Override
    public ApiResponse getConfiguration(String apiName) {
        ApiResponse apiResponse = new ApiResponse();
        FileResponse fileResponse = fileService.download(apiName+YAML_EXT);
        if(fileResponse.isSuccess()) {
            apiResponse.setStatusCode(OK_CODE);
            apiResponse.setBody(mapYamlFileToApiConfiguration(fileResponse.getFile()));
        } else {
            String message = String.format("The configuration [%s] does not exist.", apiName);
            log.warn(message);
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
        }
        return apiResponse;
    }

    @Override
    public ApiResponse deleteConfiguration(String apiName) {
        ApiResponse apiResponse = new ApiResponse();
        FileResponse fileResponse = fileService.delete(apiName+YAML_EXT);
        if(fileResponse.isSuccess()) {
            String message = String.format("The configuration [%s] was deleted.", apiName);
            apiResponse.setStatusCode(OK_CODE);
            apiResponse.setBody(ResponseError.newError(message));
        } else {
            String message = String.format("The configuration [%s] does not exist.", apiName);
            log.warn(message);
            apiResponse.setStatusCode(NOT_FOUND_CODE);
            apiResponse.setBody(ResponseError.newError(message));
        }
        return apiResponse;
    }
}
