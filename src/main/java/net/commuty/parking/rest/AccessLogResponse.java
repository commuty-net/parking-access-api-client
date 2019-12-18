package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class AccessLogResponse {

    private final String logId;

    @JsonCreator
    AccessLogResponse(@JsonProperty("logId") String logId) {
        this.logId = logId;
    }

    @JsonProperty("logId")
    public String getLogId() {
        return logId;
    }
}
