package net.commuty.parking.http;

public class CredentialsException extends ApiException {

    public CredentialsException() {
        super("Impossible to retrieve a token: Wrong credentials");
    }
}
