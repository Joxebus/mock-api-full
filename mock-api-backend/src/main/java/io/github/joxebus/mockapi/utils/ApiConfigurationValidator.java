package io.github.joxebus.mockapi.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.github.joxebus.mockapi.model.ApiConfiguration;
import io.github.joxebus.mockapi.model.ApiPath;

/**
 * Static validator for {@link ApiConfiguration} payloads. Returns the first
 * error message found, or an empty {@link Optional} when the configuration is
 * valid.
 */
public final class ApiConfigurationValidator {

    private ApiConfigurationValidator() {
    }

    public static Optional<String> validate(ApiConfiguration apiConfiguration) {
        if (apiConfiguration == null) {
            return Optional.of("The configuration must not be null.");
        }

        if (isBlank(apiConfiguration.getName())) {
            return Optional.of("The configuration 'name' must not be blank.");
        }

        Map<String, List<ApiPath>> paths = apiConfiguration.getPaths();
        if (paths == null || paths.isEmpty()) {
            return Optional.of("The configuration must contain at least one operation in 'paths'.");
        }

        for (Map.Entry<String, List<ApiPath>> entry : paths.entrySet()) {
            String operationName = entry.getKey();
            List<ApiPath> apiPaths = entry.getValue();

            if (apiPaths == null || apiPaths.isEmpty()) {
                return Optional.of(String.format("The operation '%s' must contain at least one path.", operationName));
            }

            Set<String> seenMethods = new HashSet<>();
            for (ApiPath apiPath : apiPaths) {
                if (apiPath == null || isBlank(apiPath.getMethod())) {
                    return Optional.of(String.format("The operation '%s' has a path with a blank 'method'.", operationName));
                }

                String method = apiPath.getMethod().trim().toUpperCase(Locale.ROOT);
                if (!seenMethods.add(method)) {
                    return Optional.of(String.format("The operation '%s' has duplicate method '%s'. Each method may only be defined once per operation.", operationName, method));
                }

                int statusCode = apiPath.getStatusCode();
                if (statusCode < 100 || statusCode > 599) {
                    return Optional.of(String.format("The operation '%s' has a path with an invalid 'statusCode' [%d]. It must be between 100 and 599.", operationName, statusCode));
                }
            }
        }

        if (apiConfiguration.isSecured() && isBlank(apiConfiguration.getAuthConfig())) {
            return Optional.of("The configuration is secured but 'authConfig' must not be blank.");
        }

        return Optional.empty();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
