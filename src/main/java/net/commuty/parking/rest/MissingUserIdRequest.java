package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.commuty.parking.model.UserId;
import net.commuty.parking.model.UserIdType;

class MissingUserIdRequest {

    private final String id;
    private final UserIdType type;

    MissingUserIdRequest(UserId userId) {
        this.id = userId.getId();
        this.type = userId.getType();
    }

    @JsonCreator
    MissingUserIdRequest(@JsonProperty("id") String id,
                         @JsonProperty("type") UserIdType type) {
        this.id = id;
        this.type = type;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("type")
    public UserIdType getType() {
        return type;
    }
}
