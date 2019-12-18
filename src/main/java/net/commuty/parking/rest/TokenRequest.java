package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class TokenRequest {

    private final String username;

    private final String password;

    @JsonCreator
    TokenRequest(@JsonProperty("username") String username,
                 @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }
}
