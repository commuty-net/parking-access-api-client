package net.commuty.configuration;

import net.commuty.exception.ClientBuilderException;

import java.net.URI;

public class ClientBuilder {

    private static final String DEFAULT_HOST = "https://parking-access.commuty.net";

    private URI host;
    private String username;
    private String password;
    // proxybrol

    private ClientBuilder() {

    }

    public static ClientBuilder create() {
        return new ClientBuilder();
    }

    public ClientBuilder withCredentials(String username, String password) {
        if (username == null || "".equals(username.trim())) {
            throw new ClientBuilderException("You must provide a valid username");
        }
        if (password == null) {
            throw new ClientBuilderException("You must provide a non-null password");
        }
        this.username = username;
        this.password = password;
        return this;
    }

    public ClientBuilder withHost(String host) {
        if (host == null || "".equals(host.trim())) {
            throw new ClientBuilderException("You must provide a valid host URL.");
        }
        try {
            this.host = URI.create(host);
        } catch (IllegalArgumentException e) {
            throw new ClientBuilderException("The host provided is not a valid URL.");
        }
        return this;
    }


    public static ClientBuilder defaultClient(String username, String password) {
        return create()
                .withCredentials(username, password)
                .withHost(DEFAULT_HOST);
    }

    public void validate() {
        if (this.username == null) {
            throw new ClientBuilderException("A username is required. Did you forgot to call the 'withCredentials' method ?");
        }
        if (this.password == null) {
            throw new ClientBuilderException("A password is required. Did you forgot to call the 'withCredentials' method ?");
        }
        if (this.host == null) {
            throw new ClientBuilderException("A host is required. Did you forgot to call the 'withHost' method ?");
        }
    }

    public URI getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
