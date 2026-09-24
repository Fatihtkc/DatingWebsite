package com.datingapp.backend.exception;

public class ContentPolicyViolationException extends RuntimeException {

    public ContentPolicyViolationException(String message) {
        super(message);
    }
}