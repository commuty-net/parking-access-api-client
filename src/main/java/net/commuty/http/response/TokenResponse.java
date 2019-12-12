package net.commuty.http.response;

import com.fasterxml.jackson.annotation.JsonCreator;

public class TokenResponse {

    private final String token;

    @JsonCreator
    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
