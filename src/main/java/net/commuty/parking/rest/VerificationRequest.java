package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.commuty.parking.model.UserId;

class VerificationRequest {

    private final UserId userId;

    @JsonCreator
    VerificationRequest(@JsonProperty("userId") UserId userId) {
        this.userId = userId;
    }

    @JsonProperty("userId")
    public UserId getUserId() {
        return userId;
    }
}
