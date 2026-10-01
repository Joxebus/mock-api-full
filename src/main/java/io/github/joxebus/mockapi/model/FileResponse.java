package io.github.joxebus.mockapi.model;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileResponse {
    private boolean success;
    private File file;
    private ResponseError error;

}
