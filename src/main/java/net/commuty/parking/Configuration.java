package net.commuty.parking;

import net.commuty.parking.rest.ParkingAccessRestClient;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import static java.util.Optional.ofNullable;

public class Configuration {

    private final String username;
    private final String password;
    private final URL host;
    private final Proxy proxy;

    private Configuration(String username, String password, URL host, Proxy proxy) {
        if (username == null) {
            throw new IllegalArgumentException("A username is required. Did you forgot to call the 'withCredentials' method ?");
        }
        if (password == null) {
            throw new IllegalArgumentException("A password is required. Did you forgot to call the 'withCredentials' method ?");
        }
        if (host == null) {
            throw new IllegalArgumentException("A host is required. Did you forgot to call the 'withHost' method ?");
        }
        this.username = username;
        this.password = password;
        this.host = host;
        this.proxy = proxy;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public URL getHost() {
        return host;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public ParkingAccess toRestClient() {
        return new ParkingAccessRestClient(this);
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
                throw new IllegalArgumentException("You must provide a valid username");
            }
            if (password == null) {
                throw new IllegalArgumentException("You must provide a non-null password");
            }
            this.username = username;
            this.password = password;
            return this;
        }

        public Builder withHost(String host) {
            if (host == null || host.trim().isEmpty()) {
                throw new IllegalArgumentException("You must provide a valid host URL.");
            }
            this.host = toURL(host);
            return this;
        }

        private URL toURL(String host) {
            try {
                return new URL(host);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("The host provided is not a valid URL.");
            }
        }

        public Builder withProxy(Proxy proxy) {
            if (proxy == null) {
                throw new IllegalArgumentException("Proxy cannot be null.");
            }
            this.proxy = proxy;
            return this;
        }

        public Configuration build() {
            URL hostURL = ofNullable(host).orElse(toURL(DEFAULT_HOST));
            return new Configuration(username, password, hostURL, proxy);
        }


        public static Configuration buildDefault(String username, String password) {
            return new Configuration.Builder()
                    .withCredentials(username, password)
                    .build();
        }
    }
}
