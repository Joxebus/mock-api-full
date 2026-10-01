package io.github.joxebus.mockapi.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiConfiguration {
    private String name;
    private String description;
    private String termsOfService; // URL
    private String version;
    private ApiContact contact;
    private ApiLicense license;
    private boolean secured;
    private String authConfig;
    private Map<String, List<ApiPath>> paths = new HashMap<>();

    public Optional<ApiPath> findPath(String operationName, String method) {
        List<ApiPath> apiPaths = paths.get(operationName);
        return apiPaths.stream().filter( apiPath -> method.equalsIgnoreCase(apiPath.getMethod()))
                .findFirst();
    }
}
