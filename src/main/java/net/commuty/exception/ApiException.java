package net.commuty.exception;

import net.commuty.model.Message;

public class ApiException extends Exception {

    private String message;
    private Exception cause;

    public ApiException(String message, Exception cause) {
        super(message, cause);
    }
}
