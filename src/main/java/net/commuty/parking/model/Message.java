package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Message {

    private final String reason;

    private String message;

    @JsonCreator
    private Message(String reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

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
