package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.commuty.parking.model.AccessRight;

import java.util.Collection;

class AccessRightResponse {

    private final Collection<AccessRight> accessRights;

    @JsonCreator
    AccessRightResponse(@JsonProperty("accessRights") Collection<AccessRight> accessRights) {
        this.accessRights = accessRights;
    }

    @JsonProperty("accessRights")
    public Collection<AccessRight> getAccessRights() {
        return accessRights;
    }
}
