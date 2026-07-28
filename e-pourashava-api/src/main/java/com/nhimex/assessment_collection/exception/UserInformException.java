package com.nhimex.assessment_collection.exception;

public class UserInformException extends RuntimeException {

    public UserInformException(String message) {
        super(message);
    }

    public UserInformException(String message, Throwable cause) {
        super(message, cause);
    }
}
