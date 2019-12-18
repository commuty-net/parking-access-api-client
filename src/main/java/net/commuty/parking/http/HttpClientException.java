package net.commuty.parking.http;

public class HttpClientException extends ApiException {

    protected HttpClientException(Exception cause) {
        super("The client was unable to send the query", cause);
    }
}
