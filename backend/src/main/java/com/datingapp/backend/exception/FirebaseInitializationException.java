package com.datingapp.backend.exception;

public class FirebaseInitializationException extends RuntimeException{

    public FirebaseInitializationException(String message){
        super(message);
    }

    public FirebaseInitializationException(String message, Throwable cause){
        super(message, cause);
    }
}