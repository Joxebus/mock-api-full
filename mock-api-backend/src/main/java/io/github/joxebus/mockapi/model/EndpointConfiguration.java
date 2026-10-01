package io.github.joxebus.mockapi.model;

import java.util.List;

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
public class EndpointConfiguration {
    private String config;
    private List<Endpoint> endpoints;
}
