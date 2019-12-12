package net.commuty.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDateTime;

import static net.commuty.model.AccessDirection.IN;
import static net.commuty.model.AccessDirection.OUT;

public class AccessLog {

    private final String userId;

    private final UserIdType userIdType;

    private final AccessDirection way;

    private final LocalDateTime at;

    @JsonCreator
    private AccessLog(String userId, UserIdType userIdType, AccessDirection way, LocalDateTime at) {
        this.userId = userId;
        this.userIdType = userIdType;
        this.way = way;
        this.at = at;
    }

    public static AccessLog createInAccessLog(UserId userId, LocalDateTime at) {
        return new AccessLog(userId.getId(), userId.getUserIdType(), IN, at);
    }

    public static AccessLog createOutAccessLog(UserId userId, LocalDateTime at) {
        return new AccessLog(userId.getId(), userId.getUserIdType(), OUT, at);
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
