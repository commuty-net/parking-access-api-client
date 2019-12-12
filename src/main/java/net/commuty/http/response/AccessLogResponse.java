package net.commuty.http.response;

import com.fasterxml.jackson.annotation.JsonCreator;

public class AccessLogResponse {

    private final String logId;

    @JsonCreator
    public AccessLogResponse(String logId) {
        this.logId = logId;
    }

    public String getLogId() {
        return logId;
    }
}
