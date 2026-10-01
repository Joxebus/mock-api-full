package io.github.joxebus.mockapi.service;

import javax.servlet.http.HttpServletRequest;

import io.github.joxebus.mockapi.model.ApiResponse;

public interface MockApiService {

    ApiResponse getMockApiOperation(HttpServletRequest request);

}
