package io.github.joxebus.mockapi.common;

public interface Constants {
    // File constants
    String YAML_EXT = ".yaml";

    // HTTP Status Codes
    int OK_CODE = 200;
    int CREATED_CODE = 201;
    int BAD_REQUEST_CODE = 400;
    int UNAUTHORIZED = 401;
    int NOT_FOUND_CODE = 404;
    int METHOD_NOT_ALLOWED = 405;
    int INTERNAL_SERVER_ERROR = 500;

    // HTTP Headers
    String HEADER_AUTH = "Authorization";

    // URL constants
    String SLASH = "/";
    String PATH_WILDCARD = "/**";
    String PATH_MOCK_API = "/api";
    int PATH_MOCK_API_LENGTH = PATH_MOCK_API.length();
    String PATH_CONFIG = "/config";
    String PATH_ENDPOINT = "/endpoint";
}
