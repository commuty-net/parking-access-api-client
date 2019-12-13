package net.commuty.parking.configuration;

import net.commuty.parking.exception.ClientBuilderException;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Optional;

public class Client {

    private final URL host;
    private final String username;
    private final String password;
    private Proxy proxy;


    public Client(URL host, String username, String password, Proxy proxy) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.proxy = proxy;
    }

    public URL getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Optional<Proxy> getProxy() {
        return Optional.ofNullable(proxy);
    }

    public static class Builder {

        private static final String DEFAULT_HOST = "https://parking-access.commuty.net";

        private URL host;
        private String username;
        private String password;
        private Proxy proxy;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder withCredentials(String username, String password) {
            if (username == null || username.trim().isEmpty()) {
                throw new ClientBuilderException("You must provide a valid username");
            }
            if (password == null) {
                throw new ClientBuilderException("You must provide a non-null password");
            }
            this.username = username;
            this.password = password;
            return this;
        }

        public Builder withHost(String host) {
            if (host == null || host.trim().isEmpty()) {
                throw new ClientBuilderException("You must provide a valid host URL.");
            }
            try {
                this.host = new URL(host);
            } catch (MalformedURLException e) {
                throw new ClientBuilderException("The host provided is not a valid URL.");
            }
            return this;
        }

        public Builder withProxy(Proxy proxy) {
            if (proxy == null) {
                throw new ClientBuilderException("Proxy cannot be null.");
            }
            this.proxy = proxy;
            return this;
        }

        public Client build() {
            validate();
            return new Client(host, username, password, proxy);
        }


        public static Client buildDefault(String username, String password) {
            return new Builder()
                    .withHost(DEFAULT_HOST)
                    .withCredentials(username, password)
                    .build();
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
    }
}
