package net.commuty.parking.exception;

public class HttpClientException extends ApiException {

    public HttpClientException(Exception cause) {
        super("The client was unable to send the query", cause);
    }
}
