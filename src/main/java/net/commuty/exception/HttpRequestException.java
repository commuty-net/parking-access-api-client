package net.commuty.exception;

import net.commuty.model.Message;

import static java.net.HttpURLConnection.*;

public class HttpRequestException extends ApiException {

    private final int httpResponseCode;
    private final Message errorResponse;

    public HttpRequestException(int httpResponseCode, Message errorResponse) {
        super("The request failed with an error code "+ httpResponseCode);
        this.httpResponseCode = httpResponseCode;
        this.errorResponse = errorResponse;
    }

    public Message getErrorResponse() {
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
