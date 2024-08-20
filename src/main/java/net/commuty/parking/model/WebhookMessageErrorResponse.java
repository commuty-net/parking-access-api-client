package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * When a message is sent to the webhook recipient, this class represents the expected error type to return in the HTTP body.
 */
public class WebhookMessageErrorResponse {
    private final String reason;
    private final String message;

    @JsonCreator
    public WebhookMessageErrorResponse(@JsonProperty("reason") String reason,
                                       @JsonProperty("message") String message) {
        this.reason = reason;
        this.message = message;
    }

    @JsonProperty
    public String getReason() {
        return reason;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "WebhookMessageErrorResponse{" +
                "reason='" + reason + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
