package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Defines a parking spot
 */
public class ParkingSpot {
    private final UUID id;
    private final String name;
    private final String displayName;
    private final boolean evCharger;
    private final boolean visitorSpot;

    @JsonCreator
    public ParkingSpot(@JsonProperty("id") UUID id,
                       @JsonProperty("name") String name,
                       @JsonProperty("displayName") String displayName,
                       @JsonProperty("evCharger") boolean evCharger,
                       @JsonProperty("visitorSpot") boolean visitorSpot) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.evCharger = evCharger;
        this.visitorSpot = visitorSpot;
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
}
