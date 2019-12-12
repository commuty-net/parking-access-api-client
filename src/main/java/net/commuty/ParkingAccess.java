package net.commuty;

import net.commuty.configuration.ClientBuilder;
import net.commuty.configuration.JsonMapper;
import net.commuty.exception.ApiException;
import net.commuty.exception.CredentialsException;
import net.commuty.exception.HttpClientException;
import net.commuty.exception.HttpRequestException;
import net.commuty.http.HttpClient;
import net.commuty.http.request.TokenRequest;
import net.commuty.http.request.VerificationRequest;
import net.commuty.http.response.AccessRightResponse;
import net.commuty.http.response.TokenResponse;
import net.commuty.http.response.VerificationResponse;
import net.commuty.model.AccessRight;
import net.commuty.model.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

public class ParkingAccess {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingAccess.class);

    public static final String TOKEN_REQUESTS_URL = "/v2/token-requests";
    public static final String ACCESS_REQUESTS_URL = "/v2/parking-sites/%s/access-requests";
    public static final String ACCESS_RIGHTS_URL = "/v2/access-rights";

    public static final String DAY_PARAM = "day";
    public static final String UNREAD_ONLY_PARAM = "unreadOnly";

    private final ClientBuilder builder;
    private final HttpClient httpClient;
    private String token;

    private ParkingAccess(ClientBuilder builder) {
        this.builder = builder;
        this.httpClient = new HttpClient(builder.getHost(), JsonMapper.create());
    }

    public static ParkingAccess create(String username, String password) {
        LOG.trace("Call to create a standard ParkingAccess client");
        ClientBuilder.defaultClient(username, password).validate();
        return new ParkingAccess(ClientBuilder.defaultClient(username, password));
    }

    public static ParkingAccess create(ClientBuilder builder) {
        LOG.trace("Call to create a custom ParkingAccess client");
        builder.validate();
        return new ParkingAccess(builder);
    }

    public String authenticate() throws CredentialsException, ApiException {
        LOG.debug("Authenticating user");
        try {
            TokenResponse auth = httpClient.makePostRequest(TOKEN_REQUESTS_URL, null, new TokenRequest(this.builder.getUsername(), this.builder.getPassword()), TokenResponse.class);
            token = auth.getToken();
            LOG.debug("Authentication done, token saved");
            return token;
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

    public boolean verifySingle(String parkingSiteId, UserId user) throws CredentialsException, ApiException {
        if(parkingSiteId == null || parkingSiteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking site cannot be null or blank");
        }
        LOG.debug("Verify If user {} is authorized to access the parking site {}", user, parkingSiteId);
        try {
            String path = String.format(ACCESS_REQUESTS_URL, parkingSiteId);
            VerificationResponse access = httpClient.makePostRequest(path, token, new VerificationRequest(user), VerificationResponse.class);
            return access.isGranted();
        } catch (HttpRequestException requestException) {
            if (requestException.isUnauthorized()) {
                LOG.trace("Token exception, refreshing token then try again");
                authenticate();
                return verifySingle(parkingSiteId, user);
            }
            throw new ApiException("There was an issue with the request", requestException);
        } catch (HttpClientException clientException) {
            LOG.error("There was an issue inside the client", clientException);
            throw new ApiException("The system was not able to make the request", clientException);
        }

    }

    public Collection<AccessRight> listAccessRightsForToday() throws CredentialsException, ApiException {
        return listAccessRights(null,null);
    }

    public Collection<AccessRight> listAccessRightsForToday(boolean unreadOnly) throws CredentialsException, ApiException {
        return listAccessRights(null, unreadOnly);
    }

    public Collection<AccessRight> listAccessRights(LocalDate date, Boolean unreadOnly) throws CredentialsException, ApiException {
        LOG.debug("Check the presence of Access rights");
        Map<String, String> parameters = new HashMap<>();
        if (date != null) {
            LOG.debug("Date is set to {}", date.format(ISO_LOCAL_DATE));
            parameters.put(DAY_PARAM, date.format(ISO_LOCAL_DATE));
        }

        if (unreadOnly != null) {
            LOG.debug("unreadOnly is set to {}", unreadOnly);
            parameters.put(UNREAD_ONLY_PARAM, unreadOnly.toString());
        }

        try {
            return httpClient.makeGetRequest(ACCESS_RIGHTS_URL, token, parameters, AccessRightResponse.class).getAccessRights();
        } catch (HttpRequestException requestException) {
            if (requestException.isUnauthorized()) {
                LOG.trace("Token exception, refreshing token then try again");
                authenticate();
                return listAccessRights(date, unreadOnly);
            }
            throw new ApiException("There was an issue with the request", requestException);
        } catch (HttpClientException clientException) {
            LOG.error("There was an issue inside the client", clientException);
            throw new ApiException("The system was not able to make the request", clientException);
        }

    }
}
