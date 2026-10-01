package io.github.joxebus.mockapi.service;

import java.io.File;
import java.util.List;

import io.github.joxebus.mockapi.model.FileResponse;

public interface FileService {

    FileResponse upload(String filename, File file);

    FileResponse download(String filename);

    FileResponse delete(String filename);

    List<String> filesWithExtension(String extension);
}
