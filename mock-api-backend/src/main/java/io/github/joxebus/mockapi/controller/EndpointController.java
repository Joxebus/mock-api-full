package io.github.joxebus.mockapi.controller;

import static io.github.joxebus.mockapi.common.Constants.PATH_ENDPOINT;
import static io.github.joxebus.mockapi.utils.MappingUtils.buildResponseWithHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.joxebus.mockapi.service.EndpointService;

@Controller
public class EndpointController {

    private EndpointService endpointService;

    public EndpointController(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @GetMapping(value = PATH_ENDPOINT, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> availableEndpoints() {
        return buildResponseWithHeaders(endpointService.getEndpoints());
    }

    @GetMapping(value = PATH_ENDPOINT+"/{apiName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> endpoints(@PathVariable String apiName) {
        return buildResponseWithHeaders(endpointService.getEndpoint(apiName));
    }
}
