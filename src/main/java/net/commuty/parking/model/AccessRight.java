package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.OffsetDateTime;
import java.util.Collection;

public class AccessRight {

    private final Collection<UserId> userIds;

    private final String parkingSiteId;

    private final OffsetDateTime startTime;

    private final OffsetDateTime endTime;

    private final boolean granted;

    @JsonCreator
    private AccessRight(Collection<UserId> userIds, String parkingSiteId, OffsetDateTime startTime, OffsetDateTime endTime, boolean granted) {
        this.userIds = userIds;
        this.parkingSiteId = parkingSiteId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.granted = granted;
    }

    public Collection<UserId> getUserIds() {
        return userIds;
    }

    public String getParkingSiteId() {
        return parkingSiteId;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public boolean isGranted() {
        return granted;
    }

    @Override
    public String toString() {
        return "AccessRight{" +
                "userIds=" + userIds +
                ", parkingSiteId='" + parkingSiteId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", granted=" + granted +
                '}';
    }
}
