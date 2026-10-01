package io.github.joxebus.mockapi.service;

import io.github.joxebus.mockapi.model.ApiResponse;

public interface EndpointService {

    ApiResponse getEndpoints();
    ApiResponse getEndpoint(String apiName);
}
