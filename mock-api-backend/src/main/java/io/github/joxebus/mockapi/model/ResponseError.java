package io.github.joxebus.mockapi.model;

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
public final class ResponseError {
    private String message;

    public static ResponseError newError(String errorMessage) {
        return new ResponseError(errorMessage);
    }
}
