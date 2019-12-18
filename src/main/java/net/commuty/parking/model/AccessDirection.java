package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * When reporting accesses on a parking site, this represents whether a user is coming in ({@link AccessDirection#IN}) or existing ({@link AccessDirection#OUT}) the parking site.
 */
public enum AccessDirection {
    /**
     * Indicates a user that is coming on the parking site.
     */
    @JsonProperty("in")
    IN,

    /**
     * Indicates a user that is exiting the parking site.
     */
    @JsonProperty("out")
    OUT
}
