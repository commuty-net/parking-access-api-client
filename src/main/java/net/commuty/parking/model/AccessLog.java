package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

public class AccessLog {

    private final String userId;

    private final UserIdType userIdType;

    private final AccessDirection way;

    private final LocalDateTime at;

    @JsonCreator
    private AccessLog(String userId, UserIdType userIdType, AccessDirection way, LocalDateTime at) {
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
        return new AccessLog(userId.getId(), userId.getUserIdType(), AccessDirection.IN, at);
    }

    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new AccessLog(userId.getId(), userId.getUserIdType(), AccessDirection.OUT, at);
    }

    public String getUserId() {
        return userId;
    }

    public UserIdType getUserIdType() {
        return userIdType;
    }

    public AccessDirection getWay() {
        return way;
    }

    public LocalDateTime getAt() {
        return at;
    }
}
