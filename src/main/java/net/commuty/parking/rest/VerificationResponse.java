package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class VerificationResponse {

    private final boolean granted;

    @JsonCreator
    VerificationResponse(@JsonProperty("granted") boolean granted) {
        this.granted = granted;
    }

    @JsonProperty("granted")
    public boolean isGranted() {
        return granted;
    }
}
