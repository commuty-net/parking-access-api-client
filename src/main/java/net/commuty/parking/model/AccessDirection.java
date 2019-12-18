package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccessDirection {
    @JsonProperty("in")
    IN,

    @JsonProperty("out")
    OUT
}
