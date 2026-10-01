package io.github.joxebus.mockapi.controller;

import static io.github.joxebus.mockapi.common.Constants.PATH_MOCK_API;
import static io.github.joxebus.mockapi.common.Constants.PATH_WILDCARD;
import static io.github.joxebus.mockapi.utils.MappingUtils.buildResponseWithHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.joxebus.mockapi.service.MockApiService;

@Controller
public class MockApiController {

    private final MockApiService mockApiService;

    public MockApiController(MockApiService mockApiService) {
        this.mockApiService = mockApiService;
    }

    @RequestMapping(value = PATH_MOCK_API +PATH_WILDCARD, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> mockApiForRequest(HttpServletRequest request) {
        return buildResponseWithHeaders(mockApiService.getMockApiOperation(request));
    }

}
