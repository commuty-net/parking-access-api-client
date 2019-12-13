package net.commuty.parking.exception;

public class CredentialsException extends Exception {

    public CredentialsException() {
        super("Impossible to retrieve a token: Wrong credentials");
    }
}
