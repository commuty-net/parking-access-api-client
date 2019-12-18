package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.commuty.parking.model.AccessLog;

import java.util.Collection;

class AccessLogRequest {

    private final Collection<AccessLog> accesses;

    @JsonCreator
    AccessLogRequest(@JsonProperty("accesses") Collection<AccessLog> accesses) {
        this.accesses = accesses;
    }

    @JsonProperty("accesses")
    public Collection<AccessLog> getAccesses() {
        return accesses;
    }
}
