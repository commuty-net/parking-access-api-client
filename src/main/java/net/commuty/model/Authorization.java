package net.commuty.model;

public class Authorization {

    private final String token;

    public Authorization(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
