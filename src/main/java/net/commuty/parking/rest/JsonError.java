package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.commuty.parking.http.Error;

public class JsonError implements Error {

    private final String reason;

    private final String message;

    @JsonCreator
    JsonError(@JsonProperty("reason") String reason,
              @JsonProperty("message") String message) {
        this.reason = reason;
        this.message = message;
    }

    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "reason='" + reason + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
