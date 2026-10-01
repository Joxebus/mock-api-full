package io.github.joxebus.mockapi.exception;

/**
 * Unchecked exception raised by {@code FileService} implementations when a file
 * operation cannot be completed (e.g. the target file is missing or could not be
 * written).
 */
public class FileServiceException extends RuntimeException {

    public FileServiceException(String message) {
        super(message);
    }

}
