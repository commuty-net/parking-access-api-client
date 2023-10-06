package net.commuty.parking.rest;

import net.commuty.parking.Configuration;
import net.commuty.parking.ParkingAccess;
import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.http.HttpClient;
import net.commuty.parking.http.HttpClientException;
import net.commuty.parking.http.HttpRequestException;
import net.commuty.parking.model.*;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;

public class ParkingAccessRestClient implements ParkingAccess {

    private static final Logger LOG = getLogger(ParkingAccessRestClient.class);

    public static final String TOKEN_REQUESTS_URL = "/v2/token-requests";
    public static final String ACCESS_REQUESTS_URL = "/v2/parking-sites/%s/access-requests";
    public static final String ACCESS_RIGHTS_URL = "/v2/access-rights";
    public static final String PARKING_SPOTS_RIGHTS_URL = "/v2/parking-sites/%s/parking-spots";
    public static final String APPLICATION_LOGS_URL = "/v2/application-logs";
    public static final String REPORT_ACCESS_URL = "/v2/parking-sites/%s/access-logs";
    public static final String REPORT_MISSING_IDS_URL = "/v2/missing-user-ids";
    public static final String REPORT_AVAILABLE_SPOTS_COUNT_URL = "/v2/parking-sites/%s/counts";

    public static final String DAY_PARAM = "day";
    public static final String UNREAD_ONLY_PARAM = "unreadOnly";
    public static final String DRY_RUN_PARAM = "dryRun";
    public static final String CREATED_AFTER_PARAM = "createdAfter";
    public static final String GRANTED_PARAM = "granted";
    public static final String PARKING_SITE_ID_PARAM = "parkingSiteId";
    public static final String INCLUDE_ATTRIBUTES_PARAM = "includeAttributes";
    private static final ZoneId UTC = ZoneId.of("UTC");

    private final Configuration configuration;
    private final HttpClient httpClient;
    private String token;

    public ParkingAccessRestClient(Configuration configuration) {
        this.configuration = configuration;
        this.httpClient = new HttpClient(configuration.getHost(),
                JsonMapper.create(),
                configuration.getProxy(),
                configuration.getTimeout().getConnectionTimeoutInMs(),
                configuration.getTimeout().getRequestTimeoutInMs());
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
        validateParkingSiteId(parkingSiteId);
        if(user == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        LOG.debug("Verify whether user {} is authorized to access the parking site {}", user, parkingSiteId);
        String path = String.format(ACCESS_REQUESTS_URL, parkingSiteId);
        return withRetry(() -> httpClient.makePostRequest(path, token, new VerificationRequest(user), VerificationResponse.class).isGranted());
    }

    @Override
    public Collection<AccessRight> listAccessRightsForToday() throws CredentialsException, HttpRequestException, HttpClientException {
        return listAccessRights(null, null, null, null, null, null, emptySet());
    }

    @Override
    public Collection<AccessRight> listAccessRightsForToday(boolean unreadOnly) throws CredentialsException, HttpRequestException, HttpClientException {
        return listAccessRights(null, unreadOnly, null, null, null, null, emptySet());
    }

    @Override
    public Collection<AccessRight> listAccessRights(LocalDate date,
                                                    Boolean unreadOnly,
                                                    Boolean dryRun,
                                                    LocalDateTime createdAfter,
                                                    Boolean granted,
                                                    String parkingSiteId,
                                                    Set<AccessRightAttributeName> includeAttributes) throws CredentialsException, HttpRequestException, HttpClientException {
        LOG.debug("Check the presence of Access rights");
        Map<String, Collection<String>> parameters = createListAccessRightQueryParameters(date, unreadOnly, dryRun, createdAfter, granted, parkingSiteId, includeAttributes);
        return withRetry(() -> httpClient.makeGetRequest(ACCESS_RIGHTS_URL, token, parameters, AccessRightResponse.class).getAccessRights());
    }

    private Map<String, Collection<String>> createListAccessRightQueryParameters(LocalDate date,
                                                                                 Boolean unreadOnly,
                                                                                 Boolean dryRun,
                                                                                 LocalDateTime createdAfter,
                                                                                 Boolean granted,
                                                                                 String parkingSiteId,
                                                                                 Set<AccessRightAttributeName> includeAttributes) {
        Map<String, Collection<String>> parameters = new HashMap<>();
        if (date != null) {
            String formatted = date.format(ISO_LOCAL_DATE);
            LOG.debug("Date is set to {}", formatted);
            parameters.put(DAY_PARAM, singletonList(formatted));
        }

        if (unreadOnly != null) {
            LOG.debug("unreadOnly is set to {}", unreadOnly);
            parameters.put(UNREAD_ONLY_PARAM, singletonList(unreadOnly.toString()));
        }

        if (dryRun != null) {
            LOG.debug("dryRun is set to {}", dryRun);
            parameters.put(DRY_RUN_PARAM, singletonList(dryRun.toString()));
        }

        if (granted != null) {
            LOG.debug("granted is set to {}", granted);
            parameters.put(GRANTED_PARAM, singletonList(granted.toString()));
        }

        String sanitizedParkingSiteId = parkingSiteId != null ? parkingSiteId.trim() : null;
        if (sanitizedParkingSiteId != null && !sanitizedParkingSiteId.isEmpty()) {
            LOG.debug("parkingSiteId is set to {}", sanitizedParkingSiteId);
            parameters.put(PARKING_SITE_ID_PARAM, singletonList(sanitizedParkingSiteId));
        }

        if (createdAfter != null) {
            String formatted = createdAfter.truncatedTo(SECONDS).atZone(UTC).format(ISO_INSTANT);
            LOG.debug("createdAfter is set to {}", formatted);
            parameters.put(CREATED_AFTER_PARAM, singletonList(formatted));
        }

        if (includeAttributes != null) {
            Collection<String> attributes = includeAttributes
                    .stream()
                    .map(AccessRightAttributeName::getAttributeName)
                    .collect(toList());
            parameters.put(INCLUDE_ATTRIBUTES_PARAM, attributes);
        }
        return parameters;
    }

    @Override
    public String reportAccessLog(String parkingSiteId, Collection<AccessLog> accessLogs) throws CredentialsException, HttpRequestException, HttpClientException {
        validateParkingSiteId(parkingSiteId);
        if (accessLogs == null || accessLogs.isEmpty()) {
            throw new IllegalArgumentException("Accesses cannot be null or blank");
        }
        LOG.debug("Report Access logs to Commuty for the site {}", parkingSiteId);
        String path = String.format(REPORT_ACCESS_URL, parkingSiteId);
        return withRetry(() -> httpClient.makePostRequest(path, token, new AccessLogRequest(accessLogs), AccessLogResponse.class).getLogId());
    }

    @Override
    public UserId reportMissingUserId(UserId user) throws CredentialsException, HttpRequestException, HttpClientException {
        if(user == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        LOG.debug("Report user {} as missing", user);
        return withRetry(() -> httpClient.makePostRequest(REPORT_MISSING_IDS_URL, token, new MissingUserIdRequest(user), UserId.class));
    }

    @Override
    public Count reportAvailableSpotCount(String parkingSiteId, int count, Integer total) throws CredentialsException, HttpRequestException, HttpClientException {
        validateParkingSiteId(parkingSiteId);
        LOG.debug("Report number of available spots to Commuty for the site {}", parkingSiteId);
        String path = String.format(REPORT_AVAILABLE_SPOTS_COUNT_URL, parkingSiteId);
        return withRetry(() -> httpClient.makePostRequest(path, token, new CountRequest(count, total), Count.class));
    }

    @Override
    public List<ParkingSpot> listParkingSpots(String parkingSiteId) throws CredentialsException, HttpRequestException, HttpClientException {
        if (parkingSiteId == null || parkingSiteId.trim().isEmpty()) {
            throw new IllegalArgumentException("parkingSiteId must not be null or blank");
        }

        LOG.debug("List parking spot for parkingSiteId={}", parkingSiteId);
        String path = String.format(PARKING_SPOTS_RIGHTS_URL, parkingSiteId);
        return withRetry(() -> httpClient.makeGetRequest(path, token, emptyMap(), ParkingSpotResponse.class).getParkingSpots());
    }

    @Override
    public UUID reportApplicationLog(ApplicationLog log) throws CredentialsException, HttpRequestException, HttpClientException {
        if (log == null) {
            throw new IllegalArgumentException("log must not be null");
        }

        LOG.debug("Report Application log to Commuty");
        return withRetry(() -> httpClient.makePostRequest(APPLICATION_LOGS_URL, token, log, ApplicationLogResponse.class).getLogId());
    }

    private <T> T withRetry(Callable<T> callable) throws HttpClientException, CredentialsException, HttpRequestException {
        Retry retry = new Retry(configuration.getRetryStrategy().getNumberOfRetries() + 1, configuration.getRetryStrategy().getIntervalInMs());
        while (true) {
            LOG.trace("{} retries left to call api", retry.getCount());
            try {
                token = token != null ? token : authenticate();
                return callable.call();
            } catch (HttpRequestException exception) {
                if (exception.isForbidden()) {
                    LOG.trace("Token exception, refreshing token then try again");
                    token = authenticate();
                }
                retry = retry.next();
                if (retry.isOver()) {
                    throw exception;
                } else {
                    retry.waitInterval();
                }
            } catch (CredentialsException exception) {
                LOG.trace("Invalid username/password");
                throw exception;
            } catch (HttpClientException exception) {
                LOG.trace("Issue in the client");
                throw exception;
            } catch (RuntimeException exception) {
                throw exception;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private void validateParkingSiteId(String parkingSiteId) {
        if(parkingSiteId == null || parkingSiteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking site cannot be null or blank");
        }
    }
}
