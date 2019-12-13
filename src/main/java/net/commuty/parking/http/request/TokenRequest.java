package net.commuty.parking.http.request;

public class TokenRequest implements Requestable {

    private final String username;

    private final String password;

    public TokenRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
