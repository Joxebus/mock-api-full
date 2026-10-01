package io.github.joxebus.mockapi.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.joxebus.mockapi.exception.FileServiceException;
import io.github.joxebus.mockapi.model.FileResponse;
import io.github.joxebus.mockapi.model.ResponseError;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceLocal implements FileService {

    @Value("${file.upload.folder}")
    private String fileUploadFolder;

    @PostConstruct
    private void init() {
        File file = new File(fileUploadFolder);
        if(!file.exists() && !file.mkdirs()) {
            System.exit(1);
        }
    }

    @Override
    public FileResponse upload(String filename, File file) {
        FileResponse fileResponse = new FileResponse();
        try {
            if(Objects.isNull(file)) {
                throw new FileServiceException("Failed to create empty file");
            }
            String name = filename.isBlank() ? UUID.randomUUID().toString() : filename;
            File destination = new File(fileUploadFolder, name);
            log.info("File saved at: {}", destination.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(destination);
            fos.write(Files.readAllBytes(file.getAbsoluteFile().toPath()));
            fos.close();

            fileResponse.setSuccess(true);
        } catch(Exception e) {
            fileResponse.setError(ResponseError.newError(e.getMessage()));
        }
        return fileResponse;
    }

    @Override
    public FileResponse download(String filename) {
        FileResponse fileResponse = new FileResponse();
        try {
            File downloaded = new File(fileUploadFolder, filename);
            if(!downloaded.exists()) {
                throw new FileServiceException("File ["+filename+"] doesn't exist");
            }
            fileResponse.setSuccess(true);
            fileResponse.setFile(downloaded);
        } catch(Exception e) {
            fileResponse.setError(ResponseError.newError(e.getMessage()));
        }
        return fileResponse;
    }

    @Override
    public FileResponse delete(String filename) {
        FileResponse fileResponse = new FileResponse();
        try {
            File file = new File(fileUploadFolder, filename);
            if(!file.exists()) {
                throw new FileServiceException("File ["+filename+"] doesn't exist");
            }
            Files.delete(file.toPath());
            fileResponse.setSuccess(true);
        } catch(Exception e) {
            fileResponse.setError(ResponseError.newError(e.getMessage()));
        }
        return fileResponse;
    }

    @Override
    public List<String> filesWithExtension(String extension) {
        File folder = new File(fileUploadFolder);
        return folder.list() != null ? Arrays.stream(folder.list())
                .filter(fileName -> fileName.endsWith(extension))
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
