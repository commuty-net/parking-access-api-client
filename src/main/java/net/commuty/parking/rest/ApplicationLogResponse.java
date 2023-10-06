package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

class ApplicationLogResponse {

    private final UUID logId;

    @JsonCreator
    ApplicationLogResponse(@JsonProperty("id") UUID logId) {
        this.logId = logId;
    }

    @JsonProperty("id")
    public UUID getLogId() {
        return logId;
    }
}
