package net.commuty.exception;

public class ApiException extends Exception {

    public ApiException(String message, Exception cause) {
        super(message, cause);
    }
}
