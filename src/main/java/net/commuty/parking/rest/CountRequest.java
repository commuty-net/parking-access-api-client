package net.commuty.parking.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class CountRequest {

    private final int count;
    private final Integer total;

    @JsonCreator
    CountRequest(@JsonProperty("count") int count,
                 @JsonProperty("total") Integer total) {
        this.count = count;
        this.total = total;
    }

    @JsonProperty("count")
    public int getCount() {
        return count;
    }

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }
}
