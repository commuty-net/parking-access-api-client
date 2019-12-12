package net.commuty.http.response;

import com.fasterxml.jackson.annotation.JsonCreator;

public class VerificationResponse {

    private final boolean granted;

    @JsonCreator
    VerificationResponse(boolean granted) {
        this.granted = granted;
    }

    public boolean isGranted() {
        return granted;
    }
}
