package net.commuty.parking;

import net.commuty.parking.rest.ParkingAccessRestClient;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

import static java.util.Optional.ofNullable;

/**
 * This will be your entry point to use the Rest client.
 * You need to use the inner Builder class to create a Configuration object,
 * Then you will be able to instantiate a Rest client via the method {@link #toRestClient()}.<br />
 * For example, if you want to create a client with only the username and password you were given, use this:<br />
 * <code>
 *     ParkingAccess client = Configuration.Builder.buildDefault("a-username", "a-password").toRestClient();
 * </code><br />
 * You will then be able to call methods on the <code>client</code> object.
 */
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

    /**
     * Holds the username provided at the creation of the builder.
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Holds the password provided at the creation of the builder.
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Holds the host URL provided at the creation of the builder.<br />
     * If no host was provided, this will contain the default production parking api of Commuty.
     * @return the host url the client will use.
     */
    public URL getHost() {
        return host;
    }

    /**
     * Holds the Proxy provided at the creation of the builder.<br />
     * If no proxy was provided, this will be null.
     * @return The proxy (if any).
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Create a new {@link ParkingAccess} Rest client.
     * @return the client.
     */
    public ParkingAccess toRestClient() {
        return new ParkingAccessRestClient(this);
    }

    /**
     * Build a configuration object that will be used ton instantiate a Rest client.<br />
     * To use this, call <code>Configuration.Builder.create()</code> then chain one or more builder methods.
     * The only required method is {@link #withCredentials(String, String)}.
     */
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

        /**
         * Set a username and password to authenticate against the API.<br />
         * This was provided to you by Commuty.
         * @param username The username Commuty provided to you.
         * @param password The password Commuty provided to you.
         * @return this builder instance.
         */
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

        /**
         * Allow you to define a hostname that the client will use.<br />
         * If you do not call this method, the client will use the default production parking api of Commuty.<br />
         * Unless the Commuty Team tells you otherwise, you don't need to use this method.
         * @param host a valid URL
         * @return this builder instance.
         */
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

        /**
         * Allow you to define a proxy (if it is required by your organisation).<br />
         * Pass a proxy instance to the method, i.e:<br /> <code>new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.1", 8080));</code>
         * @param proxy a valid {@link Proxy} object.
         * @return this builder instance.
         */
        public Builder withProxy(Proxy proxy) {
            if (proxy == null) {
                throw new IllegalArgumentException("Proxy cannot be null.");
            }
            this.proxy = proxy;
            return this;
        }

        /**
         * Creates a Configuration instance.<br />
         * @return a new Configuration that will allow you to create a Rest client.
         */
        public Configuration build() {
            URL hostURL = ofNullable(host).orElse(toURL(DEFAULT_HOST));
            return new Configuration(username, password, hostURL, proxy);
        }

        /**
         * Creates a new configuration instance using default values (default host and no proxy).<br />
         * In most cases, you will need only this method to create a Configuration object.
         * @param username The username Commuty provided to you.
         * @param password The password Commuty provided to you.
         * @return a new Configuration that will allow you to create a Rest client.
         */
        public static Configuration buildDefault(String username, String password) {
            return new Configuration.Builder()
                    .withCredentials(username, password)
                    .build();
        }
    }
}
