package io.github.joxebus.mockapi.service;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiResponse;

public interface ConfigurationService {

    ApiResponse createOrUpdateConfiguration(ApiConfiguration apiConfiguration);
    ApiResponse getConfiguration(String apiName);
    ApiResponse deleteConfiguration(String apiName);

}
