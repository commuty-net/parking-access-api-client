package net.commuty.parking.http;

public class ApiException extends Exception {

    protected ApiException() {}

    protected ApiException(Throwable cause) {
        super(cause);
    }

    protected ApiException(String message) {
        super(message);
    }

    protected ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
