package net.commuty;

import net.commuty.configuration.ClientBuilder;
import net.commuty.configuration.MapperBuilder;
import net.commuty.exception.ApiException;
import net.commuty.exception.CredentialsException;
import net.commuty.exception.HttpClientException;
import net.commuty.exception.HttpRequestException;
import net.commuty.http.HttpClient;
import net.commuty.http.request.TokenRequest;
import net.commuty.model.Authorization;
import net.commuty.model.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParkingAccess {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingAccess.class);

    public static final String TOKEN_REQUESTS = "/v2/token-requests";

    private final ClientBuilder builder;
    private final HttpClient httpClient;
    private String token;

    private ParkingAccess(ClientBuilder builder) {
        this.builder = builder;
        this.httpClient = new HttpClient(builder.getHost(), MapperBuilder.defaultMapper());
    }

    public static ParkingAccess create(String username, String password) {
        LOG.trace("Call to create a standard ParkingAccess client");
        ClientBuilder.defaultClient(username, password).validate();
        return new ParkingAccess(ClientBuilder.defaultClient(username, password));
    }

    public static ParkingAccess create(ClientBuilder builder) {
        LOG.trace("Call to create a custom ParkingAccess client");
        builder.validate();
        return new ParkingAccess((builder));
    }

    public void authenticate() throws CredentialsException, ApiException {
        LOG.debug("Authenticating user");
        try {
            Authorization auth = httpClient.makePostRequest(TOKEN_REQUESTS, null, new TokenRequest(this.builder.getUsername(), this.builder.getPassword()), Authorization.class);
            this.token = auth.getToken();
            LOG.debug("Authentication done, token saved");
        } catch (HttpRequestException requestException) {
            LOG.warn("Received response code other than 200");
            if (requestException.isForbidden()) {
                LOG.trace("Unable to authenticate: username or password is invalid");
                throw new CredentialsException();
            }
            throw new ApiException("There was an issue with the request", requestException);
        } catch (HttpClientException clientException) {
            LOG.error("There was an issue inside the client", clientException);
            throw new ApiException("The system was not able to make the request", clientException);
        }
    }

    public void verifyAccess(UserId user) {

    }
}
