package net.commuty.model;

public class Message {

    private final String reason;

    private String message;

    Message(String reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }
}
