package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Indicates the reason why an access (that can be granted or not) exists.
 */
public enum AccessRightReason {
    /**
     * When someone requests (and receives) a parking spot, this reason will be used.<br />
     * The access is granted.
     */
    @JsonProperty("spotReceived")
    SPOT_RECEIVED("spotReceived"),

    /**
     * When a parking spot owner releases their spot, this reason will be used.<br />
     * The access is not granted.
     */
    @JsonProperty("spotReleased")
    SPOT_RELEASED("spotReleased"),

    /**
     * When a parking spot owner is not releasing their spot, this reason will be used.<br />
     * The access is granted.
     */
    @JsonProperty("permanentAccess")
    PERMANENT_ACCESS("permanentAccess"),

    /**
     * When a user is not owner of a parking spot and is not requesting one, this reason will be used.<br />
     * The access is not granted.
     */
    @JsonProperty("none")
    NONE("none"),

    /**
     * When someone requests (and receives) a parking spot for a carpooling,
     * one user in the carpooling will have a reason <code>SPOT_RECEIVED</code>,
     * the others will have the reason <code>PASSENGER</code>.<br />
     * The access is granted.
     */
    @JsonProperty("passenger")
    PASSENGER("passenger"),

    /**
     * If a parking site is configured to always give access on the weekend,
     * users will be authorized to enter a site on the saturday or sunday, even if they didn't
     * requested a parking spot.<br />
     * The access is granted.
     */
    @JsonProperty("weekendAccess")
    WEEKEND_ACCESS("weekendAccess");

    private final String reasonName;

    AccessRightReason(String reasonName) {
        this.reasonName = reasonName;
    }

    public String getReasonName() {
        return reasonName;
    }

    public static AccessRightReason findByName(String name) {
        return Arrays.stream(values())
                .filter(reason -> reason.reasonName.equals(name))
                .findFirst()
                .orElse(null);
    }
}
