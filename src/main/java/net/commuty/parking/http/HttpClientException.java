package net.commuty.parking.http;

/**
 * This exception will occur if there was an issue before sending any request to the client (for instance, a network issue, a proxy issue,...)
 */
public class HttpClientException extends ApiException {

    protected HttpClientException(Exception cause) {
        super("The client was unable to send the query", cause);
    }
}
