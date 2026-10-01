package io.github.joxebus.mockapi.controller;

import static io.github.joxebus.mockapi.common.Constants.PATH_CONFIG;
import static io.github.joxebus.mockapi.utils.MappingUtils.buildResponseWithHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.service.ConfigurationService;

@Controller
public class ConfigurationController {

    private ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @PostMapping(value = PATH_CONFIG, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> config(@RequestBody ApiConfiguration apiConfiguration) {
        return buildResponseWithHeaders(configurationService.createOrUpdateConfiguration(apiConfiguration));
    }

    @GetMapping(value = PATH_CONFIG+"/{apiName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> configByApiName(@PathVariable String apiName) {
        return buildResponseWithHeaders(configurationService.getConfiguration(apiName));
    }

    @DeleteMapping(value = PATH_CONFIG+"/{apiName}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteConfig(@PathVariable String apiName) {
        return buildResponseWithHeaders(configurationService.deleteConfiguration(apiName));
    }
}
