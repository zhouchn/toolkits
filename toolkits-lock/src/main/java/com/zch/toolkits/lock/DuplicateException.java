package com.zch.toolkits.lock;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        this(message, null);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause, false, false);
    }
}
