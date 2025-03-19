package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

/**
 * Defines a parking spot
 */
public class ParkingSpot {
    private final UUID id;
    private final String name;
    private final String displayName;
    private final UUID zoneId;
    private final boolean evCharger;
    private final boolean visitorSpot;
    private final boolean forCarpoolersOnly;
    private final boolean forDisabled;
    private final boolean large;

    @JsonCreator
    public ParkingSpot(@JsonProperty("id") UUID id,
                       @JsonProperty("name") String name,
                       @JsonProperty("displayName") String displayName,
                       @JsonProperty("zoneId") UUID zoneId,
                       @JsonProperty("evCharger") boolean evCharger,
                       @JsonProperty("visitorSpot") boolean visitorSpot,
                       @JsonProperty("forCarpoolersOnly") boolean forCarpoolersOnly,
                       @JsonProperty("forDisabled") boolean forDisabled,
                       @JsonProperty("large") boolean large) {
        this.id = id;
        this.name = name;
        this.zoneId = zoneId;
        this.displayName = displayName;
        this.evCharger = evCharger;
        this.visitorSpot = visitorSpot;
        this.forCarpoolersOnly = forCarpoolersOnly;
        this.forDisabled = forDisabled;
        this.large = large;
    }

    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    /**
     * @return its name as defined by the company. This name is used for management.
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * @return its name as displayed to the end-user. It can be the `name` itself or another name (e.g. a zone name, a level name, etc).
     */
    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the value representing the ID of the "Parking Site" (virtual, not physical) where it is defined in Commuty.
     */
    @JsonProperty("zoneId")
    public UUID getZoneId() {
        return zoneId;
    }

    /**
     * @return whether this parking spot has an ev charger or not.
     */
    @JsonProperty("evCharger")
    public boolean isEvCharger() {
        return evCharger;
    }

    /**
     * @return whether this parking spot is dedicated to visitors or not.
     */
    @JsonProperty("visitorSpot")
    public boolean isVisitorSpot() {
        return visitorSpot;
    }

    /**
     * @return whether this parking spot is dedicated to carpoolers or not.
     */
    @JsonProperty("forCarpoolersOnly")
    public boolean isForCarpoolersOnly() {
        return forCarpoolersOnly;
    }

    /**
     * @return whether this parking spot is dedicated to people with reduced mobility or not.
     */
    @JsonProperty("forDisabled")
    public boolean isForDisabled() {
        return forDisabled;
    }

    /**
     * @return whether this parking spot is large or not.
     */
    @JsonProperty("large")
    public boolean isLarge() {
        return large;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof ParkingSpot)) {
            return false;
        } else {
            ParkingSpot that = (ParkingSpot) other;
            return Objects.equals(getId(), that.getId());
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", evCharger=" + evCharger +
                ", visitorSpot=" + visitorSpot +
                ", forCarpoolersOnly=" + forCarpoolersOnly +
                ", forDisabled=" + forDisabled +
                ", large=" + large +
                '}';
    }
}
