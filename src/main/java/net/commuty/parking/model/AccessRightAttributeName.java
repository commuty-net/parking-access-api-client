package net.commuty.parking.model;

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
    ID("id");

    private final String attributeName;

    AccessRightAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }
}
