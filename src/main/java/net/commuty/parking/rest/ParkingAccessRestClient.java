package net.commuty.parking.rest;

import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.http.HttpClient;
import net.commuty.parking.http.HttpClientException;
import net.commuty.parking.http.HttpRequestException;
import net.commuty.parking.model.AccessLog;
import net.commuty.parking.model.AccessRight;
import net.commuty.parking.model.UserId;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.slf4j.LoggerFactory.getLogger;

public class ParkingAccessRestClient implements ParkingAccess {

    private static final Logger LOG = getLogger(ParkingAccessRestClient.class);

    public static final String TOKEN_REQUESTS_URL = "/v2/token-requests";
    public static final String ACCESS_REQUESTS_URL = "/v2/parking-sites/%s/access-requests";
    public static final String ACCESS_RIGHTS_URL = "/v2/access-rights";
    public static final String REPORT_ACCESS_URL = "/v2/parking-sites/%s/access-logs";
    public static final String REPORT_MISSING_IDS_URL = "/v2/missing-user-ids";

    public static final String DAY_PARAM = "day";
    public static final String UNREAD_ONLY_PARAM = "unreadOnly";

    private final Configuration configuration;
    private final HttpClient httpClient;
    private String token;

    public ParkingAccessRestClient(Configuration configuration) {
        this.configuration = configuration;
        this.httpClient = new HttpClient(configuration.getHost(), JsonMapper.create(), configuration.getProxy());
    }

    @Override
    public String authenticate() throws CredentialsException, HttpRequestException, HttpClientException {
        LOG.debug("Authenticating user");
        try {
            TokenResponse auth = httpClient.makePostRequest(TOKEN_REQUESTS_URL, null, new TokenRequest(this.configuration.getUsername(), this.configuration.getPassword()), TokenResponse.class);
            token = auth.getToken();
            LOG.debug("Authentication done, token saved");
            return token;
        } catch (HttpRequestException requestException) {
            LOG.warn("Received response code other than 200");
            if (requestException.isForbidden()) {
                LOG.trace("Unable to authenticate: username or password is invalid");
                throw new CredentialsException();
            } else {
                throw requestException;
            }
        }
    }

    @Override
    public boolean isGranted(String parkingSiteId, UserId user) throws CredentialsException, HttpRequestException, HttpClientException {
        if(parkingSiteId == null || parkingSiteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking site cannot be null or blank");
        }
        if(user == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        LOG.debug("Verify whether user {} is authorized to access the parking site {}", user, parkingSiteId);
        try {
            String path = String.format(ACCESS_REQUESTS_URL, parkingSiteId);
            VerificationResponse access = httpClient.makePostRequest(path, token, new VerificationRequest(user), VerificationResponse.class);
            return access.isGranted();
        } catch (HttpRequestException requestException) {
            if (requestException.isUnauthorized()) {
                LOG.trace("Token exception, refreshing token then try again");
                authenticate();
                return isGranted(parkingSiteId, user);
            } else {
                throw requestException;
            }
        }
    }

    @Override
    public Collection<AccessRight> listAccessRightsForToday() throws CredentialsException, HttpRequestException, HttpClientException {
        return listAccessRights(null, null);
    }

    @Override
    public Collection<AccessRight> listAccessRightsForToday(boolean unreadOnly) throws CredentialsException, HttpRequestException, HttpClientException {
        return listAccessRights(null, unreadOnly);
    }

    @Override
    public Collection<AccessRight> listAccessRights(LocalDate date, Boolean unreadOnly) throws CredentialsException, HttpRequestException, HttpClientException {
        LOG.debug("Check the presence of Access rights");
        Map<String, String> parameters = createListAccessRightQueryParameters(date, unreadOnly);

        try {
            return httpClient.makeGetRequest(ACCESS_RIGHTS_URL, token, parameters, AccessRightResponse.class).getAccessRights();
        } catch (HttpRequestException requestException) {
            if (requestException.isUnauthorized()) {
                LOG.trace("Token exception, refreshing token then try again");
                authenticate();
                return listAccessRights(date, unreadOnly);
            } else {
                throw requestException;
            }
        }
    }

    private Map<String, String> createListAccessRightQueryParameters(LocalDate date, Boolean unreadOnly) {
        Map<String, String> parameters = new HashMap<>();
        if (date != null) {
            LOG.debug("Date is set to {}", date.format(ISO_LOCAL_DATE));
            parameters.put(DAY_PARAM, date.format(ISO_LOCAL_DATE));
        }

        if (unreadOnly != null) {
            LOG.debug("unreadOnly is set to {}", unreadOnly);
            parameters.put(UNREAD_ONLY_PARAM, unreadOnly.toString());
        }
        return parameters;
    }

    @Override
    public String reportAccessLog(String parkingSiteId, Collection<AccessLog> accessLogs) throws CredentialsException, HttpRequestException, HttpClientException {
        if(parkingSiteId == null || parkingSiteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking site cannot be null or blank");
        }
        if (accessLogs == null || accessLogs.isEmpty()) {
            throw new IllegalArgumentException("Accesses cannot be null or blank");
        }
        LOG.debug("Report Access logs to Commuty for the site {}", parkingSiteId);

        try {
            String path = String.format(REPORT_ACCESS_URL, parkingSiteId);
            return httpClient.makePostRequest(path, token, new AccessLogRequest(accessLogs), AccessLogResponse.class).getLogId();
        } catch (HttpRequestException requestException) {
            if (requestException.isUnauthorized()) {
                LOG.trace("Token exception, refreshing token then try again");
                authenticate();
                return reportAccessLog(parkingSiteId, accessLogs);
            } else {
                throw requestException;
            }
        }
    }

    @Override
    public UserId reportMissingUserId(UserId user) throws CredentialsException, HttpRequestException, HttpClientException {
        if(user == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        LOG.debug("Report user {} as missing", user);

        try {
            return httpClient.makePostRequest(REPORT_MISSING_IDS_URL, token, new MissingUserIdRequest(user), UserId.class);
        } catch (HttpRequestException requestException) {
            if (requestException.isUnauthorized()) {
                LOG.trace("Token exception, refreshing token then try again");
                authenticate();
                return reportMissingUserId(user);
            } else {
                throw requestException;
            }
        }
    }
}