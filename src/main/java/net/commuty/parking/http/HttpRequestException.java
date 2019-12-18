package net.commuty.parking.http;

import static java.net.HttpURLConnection.*;

public class HttpRequestException extends ApiException {

    private final int httpResponseCode;
    private final Error errorResponse;

    protected HttpRequestException(int httpResponseCode, Error errorResponse) {
        super("The request failed with an error code "+ httpResponseCode);
        this.httpResponseCode = httpResponseCode;
        this.errorResponse = errorResponse;
    }

    public Error getErrorResponse() {
        return errorResponse;
    }

    public boolean isForbidden() {
        return httpResponseCode == HTTP_FORBIDDEN;
    }

    public boolean isUnauthorized() {
        return httpResponseCode == HTTP_UNAUTHORIZED;
    }

    public boolean isBadRequest() {
        return httpResponseCode == HTTP_BAD_REQUEST;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }
}
