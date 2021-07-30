package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.commuty.parking.model.ParkingSpot;

import java.util.List;

class ParkingSpotResponse {

    private final List<ParkingSpot> parkingSpots;

    @JsonCreator
    ParkingSpotResponse(@JsonProperty("parkingSpots") List<ParkingSpot> parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    @JsonProperty("parkingSpots")
    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }
}
