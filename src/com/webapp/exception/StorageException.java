package com.webapp.exception;

import java.io.Serial;

public class StorageException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 842445699062751470L;

    private final String uuid;

    public StorageException(String message) {
        this(message, null, null);
    }

    public StorageException(String message, Exception e) {
        this(message, null, e);
    }

    public StorageException(Exception e) {
        this(e.getMessage(),e);
    }

    public String getUuid() {
        return uuid;
    }

    public StorageException(String message, String uuid) {
        super(message);
        this.uuid = uuid;
    }

    public StorageException(String message, String uuid, Exception e) {
        super(message, e);
        this.uuid = uuid;
    }
}
