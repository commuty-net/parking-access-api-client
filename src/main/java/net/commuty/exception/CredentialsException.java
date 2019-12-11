package net.commuty.exception;

public class CredentialsException extends Exception {

    public CredentialsException() {
        super("Impossible to retrieve a token: Wrong credentials");
    }
}
