package net.commuty.exception;

public class HttpClientException extends ApiException {

    public HttpClientException(Exception cause) {
        super("The client was unable to send the query", cause);
    }
}
