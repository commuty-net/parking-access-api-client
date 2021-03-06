package net.commuty.parking;

import net.commuty.parking.http.CredentialsException;
import net.commuty.parking.http.HttpClientException;
import net.commuty.parking.http.HttpRequestException;
import net.commuty.parking.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

/**
 * <p>This is the client you must use to query the parking access api of Commuty.</p>
 * <p>To create this client, use the {@link net.commuty.parking.Configuration.Builder}
 * (via the {@link Configuration.Builder#create()} or {@link Configuration.Builder#buildDefault(String, String)}.</p>
 */
public interface ParkingAccess {

    /**
     * <p>Retrieve a token based on the username and password you provided at the creation of the Builder.</p>
     * <p>By calling this method, the client will fetch a token and then save a local copy of it for further queries.</p>
     * <p>You don't need to use this method as the client will handle authentication for you.</p>
     * @return The token generated by the api The token is valid for 24 hours.
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    String authenticate() throws CredentialsException, HttpRequestException, HttpClientException;

    /**
     * <p>Check whether a {@link UserId} is allowed to enter the parking site (or not) when the request is made.</p>
     * <p>If a user has more thant one identifier known by Commuty (i.e. a badge code and a number plate), only one identifier is required to check its grant status.</p>
     * @param parkingSiteId The identifier of the parking site that was given by Commuty.
     * @param user The {@link UserId} to check.
     * @return <code>true</code> if the user is allowed on the site, <code>false</code> otherwise.
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    boolean isGranted(String parkingSiteId, UserId user) throws CredentialsException, HttpRequestException, HttpClientException;

    /**
     * <p>List all users (identified by one or more {@link UserId}) that are <b>allowed</b> or <b>denied</b> on the parking site today.</p>
     * <p>This will return all accesses of the current day, even if they were already retrieved.</p>
     * @return One or more {@link AccessRight}. Each user known by Commuty will at least have one access (granted or not). A user can have multiple accesses.
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    Collection<AccessRight> listAccessRightsForToday() throws CredentialsException, HttpRequestException, HttpClientException;

    /**
     * <p>List all users (identified by one or more {@link UserId}) that are <b>allowed</b> or <b>denied</b> on the parking site today.</p>
     * <p>If <code>unreadOnly</code> is <code>false</code>, all the accesses of the current day will be returned, even if they were already retrieved. This is the same as calling {@link #listAccessRightsForToday()}.</p>
     * <p>If <code>unreadOnly</code> is <code>true</code>, only the new accesses (the ones that were never retrieved via an api call) will be listed.</p>
     * @param unreadOnly Whether you want to retrieve unread only accesses (<code>true</code>) or all accesses (<code>false</code>)
     * @return One or more {@link AccessRight}. Each user known by Commuty will at least have one access (granted or not). A user can have multiple accesses.
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    Collection<AccessRight> listAccessRightsForToday(boolean unreadOnly) throws CredentialsException, HttpRequestException, HttpClientException;

    /**
     * <p>List all users (identified by one or more {@link UserId}) that are <b>allowed</b> or <b>denied</b> on the parking site for a given day.</p>
     * <p>The <code>date</code> parameter will filter accesses that occurs on or after midnight of the given day and before midnight of the next day.</p>
     * <p>If <code>unreadOnly</code> is <code>false</code>, all the accesses of the current day will be returned, even if they were already retrieved. This is the same as calling {@link #listAccessRightsForToday()}.</p>
     * <p>If <code>unreadOnly</code> is <code>true</code>, only the new accesses (the ones that were never retrieved via an api call) will be listed.</p>
     * @param date The {@link LocalDate} of the events you want to retrieve. If <code>date</code> is null, the accesses of the current day will be retrieved.
     * @param unreadOnly Whether you want to retrieve unread only accesses (<code>true</code>) or all accesses (<code>false</code>). If <code>unreadOnly</code> is null, all the accesses (read and unread) will be retrieved.
     * @param dryRun Whether you want to prevent to flag the retrieve accesses as "read" (<code>true</code>) or not (<code>false</code>). If <code>unreadOnly</code> is null, all the accesses retrieved will be flagged as "read".
     * @param createdAfter Only retrieve events created after this {@link LocalDateTime}. Represents a <code>UTC</code> timestamp. If <code>createdAfter</code> is null, all the accesses will be retrieved.
     * @param includeAttributes Whether you want to fetch extra attributes about each access. The possible values are listed in {@link AccessRightAttributeName}.
     * @return One or more {@link AccessRight}. Each user known by Commuty will at least have one access (granted or not). A user can have multiple accesses.
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    Collection<AccessRight> listAccessRights(LocalDate date, Boolean unreadOnly, Boolean dryRun, LocalDateTime createdAfter, Set<AccessRightAttributeName> includeAttributes) throws CredentialsException, HttpRequestException, HttpClientException;

    /**
     * <p>Report to Commuty one or more {@link AccessLog} of users that entered/exited the parking site.</p>
     * <p>This will allow Commuty to create attendance reports for parking site owners.</p>
     * <p>Upon success of this request, Commuty will reply with an unique identifier (an UUID). You can keep this identifier when Communicating with Commuty if there is an issue.</p>
     * @param parkingSiteId The identifier of the parking site that was given by Commuty.
     * @param accessLogs A collection of one or more {@link AccessLog}.
     * @return An identifier of the request you made. This can be kept for further reference
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details. This exception can happen if the <code>parkingSiteId</code> is not known.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    String reportAccessLog(String parkingSiteId, Collection<AccessLog> accessLogs) throws CredentialsException, HttpRequestException, HttpClientException;

    /**
     * <p>Report any user (identified by an id and a {@link net.commuty.parking.model.UserIdType}) that is known by you but not by Commuty.</p>
     * <p>This will allow Commuty to display a list of problematic users to parking site owners.</p>
     * @param user The {@link UserId} to report to Commuty.
     * @return  The reported user.
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    UserId reportMissingUserId(UserId user) throws CredentialsException, HttpRequestException, HttpClientException;

    /**
     * <p>Report the number of available spots (and optionally the total number of spots) on a parking site.</p>
     * @param parkingSiteId The identifier of the parking site that was given by Commuty.
     * @param count The number of available spots at the time of the submission.
     * @param total The total number of spots (the size of the parking site) at the time of the submission. Ca be <code>null</code>.
     * @return The reported counts.
     * @throws CredentialsException Your username or password is invalid.
     * @throws HttpRequestException The query was sent to the api but the status is unsuccessful (HTTP status code &ge; 400). See {@link HttpRequestException} for more details.
     * @throws HttpClientException The query did not reached the api, i.e. there was a network issue.
     */
    Count reportAvailableSpotCount(String parkingSiteId, int count, Integer total) throws CredentialsException, HttpRequestException, HttpClientException;
}
