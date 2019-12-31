package net.commuty.parking.http;

import static java.net.HttpURLConnection.*;

/**
 * <p>This exception will occur when the api accepted the request but returned a HTTP status 400 or above.</p>
 * <p>You can get the HTTP status code via {@link #getHttpResponseCode()}. Some helpers methods are present to handle some cases:</p>
 * <ul>
 *     <li>{@link #isForbidden()}: http status 403</li>
 *     <li>{@link #isUnauthorized()}: http status 401</li>
 *     <li>{@link #isBadRequest()}: http status 400</li>
 * </ul>
 * <p>Sometimes, you will get more information via the {@link #getErrorResponse()} method. This exposes a reason and message property. For more information, consult the api documentation.</p>
 */
public class HttpRequestException extends ApiException {

    private final int httpResponseCode;
    private final Error errorResponse;

    protected HttpRequestException(int httpResponseCode, Error errorResponse) {
        super("The request failed with an error code "+ httpResponseCode);
        this.httpResponseCode = httpResponseCode;
        this.errorResponse = errorResponse;
    }

    /**
     * Returns more information linked with the request exception. Can be null.
     */
    public Error getErrorResponse() {
        return errorResponse;
    }

    /**
     * Indicates if the request is forbidden (http 403).
     */
    public boolean isForbidden() {
        return httpResponseCode == HTTP_FORBIDDEN;
    }

    /**
     * Indicates if the request is unauthorized (http 401).
     */
    public boolean isUnauthorized() {
        return httpResponseCode == HTTP_UNAUTHORIZED;
    }

    /**
     * Indicates if the request is a bad request (http 400).
     */
    public boolean isBadRequest() {
        return httpResponseCode == HTTP_BAD_REQUEST;
    }

    /**
     * Returns the http status code.
     */
    public int getHttpResponseCode() {
        return httpResponseCode;
    }
}
