package io.github.joxebus.mockapi.model;

import java.util.Collections;
import java.util.Map;

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
public class ApiResponse {
    private int statusCode;
    private Object body;
    private Map<String, String[]> headers = Collections.emptyMap();
}
