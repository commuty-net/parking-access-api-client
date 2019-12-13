package net.commuty.parking.http.request;

import net.commuty.parking.model.UserId;
import net.commuty.parking.model.UserIdType;

public class MissingUserIdRequest implements Requestable{

    private final String id;
    private final UserIdType type;

    public MissingUserIdRequest(UserId userId) {
        this.id = userId.getId();
        this.type = userId.getUserIdType();
    }

    public String getId() {
        return id;
    }

    public UserIdType getType() {
        return type;
    }
}
