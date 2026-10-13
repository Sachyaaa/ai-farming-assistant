package com.sachin.ai_farming_assistant.chat.exception;

public class FarmNotFoundException
        extends RuntimeException {

    public FarmNotFoundException(String message) {
        super(message);
    }
}
