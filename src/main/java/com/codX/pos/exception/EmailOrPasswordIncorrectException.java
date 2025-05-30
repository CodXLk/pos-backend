package com.codX.pos.exception;

public class EmailOrPasswordIncorrectException extends RuntimeException {
    public EmailOrPasswordIncorrectException(String message) {
        super(message);
    }
}
