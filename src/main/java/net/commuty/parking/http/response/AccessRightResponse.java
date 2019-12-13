package net.commuty.parking.http.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.commuty.parking.model.AccessRight;

import java.util.Collection;

public class AccessRightResponse {

    private final Collection<AccessRight> accessRights;

    @JsonCreator
    public AccessRightResponse(Collection<AccessRight> accessRights) {
        this.accessRights = accessRights;
    }

    public Collection<AccessRight> getAccessRights() {
        return accessRights;
    }
}
