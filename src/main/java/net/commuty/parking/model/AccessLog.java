package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import static net.commuty.parking.model.AccessDirection.IN;
import static net.commuty.parking.model.AccessDirection.OUT;

/**
 * <p>This corresponds to a user that entered or exited a parking site at a point in time.</p>
 *
 * <p>You can construct this entity using the {@link #createInAccessLog(UserId, LocalDateTime)} or
 * {@link #createOutAccessLog(UserId, LocalDateTime)} methods, depending on the type of access log you want to report.</p>
 */
public class AccessLog {

    private final String userId;

    private final UserIdType userIdType;

    private final AccessDirection way;

    private final LocalDateTime at;

    private final boolean granted;

    private final String reason;

    @JsonCreator
    AccessLog(@JsonProperty("userId") String userId,
              @JsonProperty("userIdType") UserIdType userIdType,
              @JsonProperty("way") AccessDirection way,
              @JsonProperty("at") LocalDateTime at,
              @JsonProperty("granted") boolean granted,
              @JsonProperty("reason") String reason) {
        if (at == null) {
            throw new IllegalArgumentException("Log date cannot be null");
        }
        this.userId = userId;
        this.userIdType = userIdType;
        this.way = way;
        this.at = at;
        this.granted = granted;
        this.reason = reason;
    }

    AccessLog(String userId,
              UserIdType userIdType,
              AccessDirection way,
              LocalDateTime at) {
        this(userId, userIdType, way, at, true, null);
    }

    /**
     * Create a report for a user that entered the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user entered the parking site, in UTC. Cannot be null.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createInAccessLog(UserId userId, LocalDateTime at) {
        return createInAccessLog(userId, at, true, null);
    }

    /**
     * Create a report for a user that exited the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user exited the parking site, in UTC. Cannot be null.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at) {
        return createOutAccessLog(userId, at, true, null);
    }

    /**
     * Create a report for a user that entered the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user entered the parking site, in UTC. Cannot be null.
     * @param granted Whether or not the user was allowed to enter the parking or not;
     * @param reason reason to specify why the user has been authorized (or not) to enter. This also can be used as a free-text field for ad-hoc comments.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createInAccessLog(UserId userId, LocalDateTime at, boolean granted, String reason) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), IN, at, granted, reason);
    }

    /**
     * Create a report for a user that exited the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user exited the parking site, in UTC. Cannot be null.
     * @param granted Whether or not the user was allowed to enter the parking or not;
     * @param reason reason to specify why the user has been authorized (or not) to exit. This also can be used as a free-text field for ad-hoc comments.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at, boolean granted, String reason) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), OUT, at, granted, reason);
    }


    /**
     * The identifier of the user. This is linked with the {@link #getUserIdType()} property.
     */
    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    /**
     * The {@link UserIdType} of the identifier of the user. This is linked with the {@link #getUserId()} property.
     */
    @JsonProperty("userIdType")
    public UserIdType getUserIdType() {
        return userIdType;
    }

    /**
     * The direction (either {@link AccessDirection#IN} or {@link AccessDirection#OUT}) of the user entering/exiting the parking site.
     */
    @JsonProperty("way")
    public AccessDirection getWay() {
        return way;
    }

    /**
     * The moment when the user entered/exited the parking site.
     */
    @JsonProperty("at")
    public LocalDateTime getAt() {
        return at;
    }

    /**
     * Whether or not the user has been granted to enter/exit the parking.
     */
    @JsonProperty("granted")
    public boolean isGranted() {
        return granted;
    }

    /**
     * An optional reason to specify why the user has been authorized (or not) to enter. This also can be used as a free-text field for ad-hoc comments.
     */
    @JsonProperty("reason")
    public String getReason() {
        return reason;
    }
}
