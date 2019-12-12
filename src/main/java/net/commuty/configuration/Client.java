package net.commuty.configuration;

import net.commuty.exception.ClientBuilderException;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Optional;

public class Client {

    private URL host;
    private String username;
    private String password;
    private Proxy proxy;

    public static class Builder {

        private static final String DEFAULT_HOST = "https://parking-access.commuty.net";

        private URL host;
        private String username;
        private String password;
        private Proxy proxy;

        public Builder() {
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
            Client client = new Client();
            client.username = this.username;
            client.password = this.password;
            client.host = this.host;
            client.proxy = this.proxy;

            return client;
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
}
