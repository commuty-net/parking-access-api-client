package net.commuty.exception;

public class ApiException extends Exception {

    public ApiException() {
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
