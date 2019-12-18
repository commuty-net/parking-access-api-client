package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class TokenResponse {

    private final String token;

    @JsonCreator
    TokenResponse(@JsonProperty("token") String token) {
        this.token = token;
    }

    @JsonProperty("token")
    public String getToken() {
        return token;
    }
}
