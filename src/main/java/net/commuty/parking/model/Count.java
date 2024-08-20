package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>This corresponds to the number of available spots (and the total number of spots if provided) on a parking site.</p>
 */
public class Count {

    private final int count;
    private final Integer total;

    @JsonCreator
    Count(@JsonProperty("count") int count,
          @JsonProperty("total") Integer total) {
        this.count = count;
        this.total = total;
    }

    /**
     * The number of available spots that you submitted
     */
    @JsonProperty("count")
    public int getCount() {
        return count;
    }

    /**
     * The total number of spots on the parking site, or null if you omitted this value.
     */
    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Count{" +
                "count=" + count +
                ", total=" + total +
                '}';
    }
}
