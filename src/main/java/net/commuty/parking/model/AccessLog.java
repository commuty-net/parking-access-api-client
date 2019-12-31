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

    @JsonCreator
    AccessLog(@JsonProperty("userId") String userId,
              @JsonProperty("userIdType") UserIdType userIdType,
              @JsonProperty("way") AccessDirection way,
              @JsonProperty("at") LocalDateTime at) {
        if (at == null) {
            throw new IllegalArgumentException("Log date cannot be null");
        }
        this.userId = userId;
        this.userIdType = userIdType;
        this.way = way;
        this.at = at;
    }

    /**
     * Create a report for a user that entered the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user entered the parking site, in UTC. Cannot be null.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createInAccessLog(UserId userId, LocalDateTime at) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), IN, at);
    }

    /**
     * Create a report for a user that exited the parking site at the specified time.
     * @param userId The {@link UserId} concerned by the access log. Cannot be null.
     * @param at The moment when the user exited the parking site, in UTC. Cannot be null.
     * @return the {@link AccessLog} entity.
     */
    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), OUT, at);
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
}
