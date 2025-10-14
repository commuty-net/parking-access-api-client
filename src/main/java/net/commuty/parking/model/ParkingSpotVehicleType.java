package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Indicates the type of vehicle the parking spot is for.
 */
public enum ParkingSpotVehicleType {

    @JsonProperty("car")
    @JsonEnumDefaultValue
    CAR("car"),

    @JsonProperty("motorcycle")
    MOTORCYCLE("motorcycle"),

    @JsonProperty("bicycle")
    BICYCLE("bicycle");

    private final String vehicleType;

    ParkingSpotVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public static ParkingSpotVehicleType findByName(String name) {
        return Arrays.stream(values())
                .filter(vehicleType -> vehicleType.vehicleType.equals(name))
                .findFirst()
                .orElse(null);
    }
}
