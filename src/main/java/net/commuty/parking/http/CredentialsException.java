package net.commuty.parking.http;

/**
 * If your username or password is invalid, this exception will occur.
 */
public class CredentialsException extends ApiException {

    public CredentialsException() {
        super("Impossible to retrieve a token: Wrong credentials");
    }
}
