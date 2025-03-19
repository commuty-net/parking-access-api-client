package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Extra attributes that can be retrieved when requesting accesses.
 */
public enum AccessRightAttributeName {
    /**
     * An {@link AccessRightReason} indicating why an access was created.
     */
    @JsonProperty("reason")
    REASON("reason"),

    /**
     * A unique {@link java.util.UUID} for each access.
     */
    @JsonProperty("id")
    ID("id"),

    /**
     * When an access right is granted for a specific parking spot, this value represents its unique identifier ({@link java.util.UUID}). It is null otherwise.
     * An access right may have a parking spot, according to its reason.
     * Access rights with these reasons never owns a parking spot: <code>none</code>, <code>weekendAccess</code>, <code>spotReleased</code>
     * Access rights with these reasons may have a parking spot: <code>spotReceived</code>, <code>permanentAccess</code>, <code>passenger</code>
     */
    @JsonProperty("parkingSpotId")
    PARKING_SPOT_ID("parkingSpotId"),

    /**
     * This name is used for management.
     */
    @JsonProperty("parkingSpotName")
    PARKING_SPOT_NAME("parkingSpotName"),

    /**
     * This display name is used for end-user. It can be the <code>parkingSiteName</code>parkingSiteName itself or another name (e.g. a zone name, a level name, etc).
     */
    @JsonProperty("parkingSpotDisplayName")
    PARKING_SPOT_DISPLAY_NAME("parkingSpotDisplayName"),

    /**
     * This value represents the ID of the "Parking Site" (virtual, not physical) where it is defined in Commuty.
     */
    @JsonProperty("parkingSpotZoneId")
    PARKING_SPOT_ZONE_ID("parkingSpotZoneId"),

    /**
     * <code>true</code> when this access right is granted for a visitor. <code>false</code> otherwise.
     */
    @JsonProperty("isVisitor")
    IS_VISITOR("isVisitor"),

    /**
     * A unique identifier identifying the actual "subject" whose the access right belongs to. This can be the ID of a user, a visitor or an access exception.
     */
    @JsonProperty("subjectId")
    SUBJECT_ID("subjectId"),

    /**
     * A label describing the "subject" whose the access right belongs to.
     * This is usually the full name of a user, a visitor or an access exception.
     */
    @JsonProperty("subjectLabel")
    SUBJECT_LABEL("subjectLabel"),

    /**
     * Only used when parsed from a unknown object key.
     */
    @JsonEnumDefaultValue
    @JsonProperty("unknown")
    UNKNOWN("unknown")
    ;

    private final String attributeName;

    AccessRightAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
