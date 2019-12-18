package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import static net.commuty.parking.model.AccessDirection.IN;
import static net.commuty.parking.model.AccessDirection.OUT;

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

    public static AccessLog createInAccessLog(UserId userId, LocalDateTime at) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), IN, at);
    }

    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getType(), OUT, at);
    }

    @JsonProperty("userId")
    public String getUserId() {
        return userId;
    }

    @JsonProperty("userIdType")
    public UserIdType getUserIdType() {
        return userIdType;
    }

    @JsonProperty("way")
    public AccessDirection getWay() {
        return way;
    }

    @JsonProperty("at")
    public LocalDateTime getAt() {
        return at;
    }
}
