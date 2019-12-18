package net.commuty.parking.http;

/**
 * Any Exception that the client can throw.<br />
 * You can either catch This exception, or use the more specific exceptions ({@link CredentialsException}, {@link HttpRequestException}, {@link HttpClientException}).
 */
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
