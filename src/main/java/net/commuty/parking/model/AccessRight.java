package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.emptyMap;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static net.commuty.parking.model.AccessRightAttributeName.*;

/**
 * This holds the access right:
 * <ul>
 *     <li>for a user identified by one or more of its ids;</li>
 *     <li>for a specific parking site;</li>
 *     <li>for a period of time (starting from {@link #getStartTime()} until before {@link #getEndTime()});</li>
 *     <li>whether is access is allowed or not.</li>
 * </ul>
 *
 * <p>An access right will never overlap two days, meaning that it will always start on or after midnight and end on or before midnight the next day.</p>
 * <p>It is possible for a user to have multiple access rights on a parking site for a day.</p>
 * <p>In this case, the periods (from start time until end time) will also never overlap. Moreover, all the periods will cover a complete day.</p>
 *
 * <p>For instance:</p>
 * A user that is <b>not granted</b> today will:
 * <ul>
 *     <li>have one <b>granted</b> right starting from <b>today midnight</b> until <b>tomorrow midnight</b></li>
 * </ul>
 * A user that is <b>granted</b> today <b>from 08:00 until 16:00</b> will:
 * <ul>
 *     <li>have one <b>not granted</b> right starting from <b>today midnight</b> until <b>today 08:00</b></li>
 *     <li>have one <b>granted</b> right starting from <b>today 08:00</b> until <b>today 16:00</b></li>
 *     <li>have one <b>not granted</b> right starting from <b>today 16:00</b> until <b>tomorrow midnight</b></li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessRight {

    private final Collection<UserId> userIds;

    private final String parkingSiteId;

    private final OffsetDateTime startTime;

    private final OffsetDateTime endTime;

    private final boolean granted;

    private final Map<AccessRightAttributeName, String> attributes;

    @JsonCreator
    public AccessRight(@JsonProperty("userIds") Collection<UserId> userIds,
                       @JsonProperty("parkingSiteId") String parkingSiteId,
                       @JsonProperty("startTime") OffsetDateTime startTime,
                       @JsonProperty("endTime") OffsetDateTime endTime,
                       @JsonProperty("granted") boolean granted,
                       @JsonProperty("attributes") Map<AccessRightAttributeName, String> attributes) {
        this.userIds = userIds;
        this.parkingSiteId = parkingSiteId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.granted = granted;
        this.attributes = ofNullable(attributes).orElse(emptyMap());
    }

    /**
     * A list of known user ids known by Commuty that target the user concerning by this access right.
     */
    @JsonProperty("userIds")
    public Collection<UserId> getUserIds() {
        return userIds;
    }

    /**
     * The identifier of the parking site that the user is able (or not) to enter.
     */
    @JsonProperty("parkingSiteId")
    public String getParkingSiteId() {
        return parkingSiteId;
    }

    /**
     * The moment (included) when the user is allowed (or not) on the parking site.
     */
    @JsonProperty("startTime")
    public OffsetDateTime getStartTime() {
        return startTime;
    }

    /**
     * The moment (excluded) when the user is allowed (or not) anymore on the parking site.
     */
    @JsonProperty("endTime")
    public OffsetDateTime getEndTime() {
        return endTime;
    }

    /**
     * The status of the access right, whether the user is allowed (<code>true</code>) on the parking site or not (<code>false</code>).
     */
    @JsonProperty("granted")
    public boolean isGranted() {
        return granted;
    }

    /**
     * Extra attributes of the access right. It is present only when <code>includeAttributes</code> contains at least one {@link AccessRightAttributeName} when fetching access rights.<br />
     * Otherwise, this list is empty.
     */
    @JsonProperty("attributes")
    @JsonInclude(NON_EMPTY)
    public Map<AccessRightAttributeName, String> getAttributes() {
        return attributes;
    }

    /**
     * The unique identifier of this access right. Only present when the extra attributes are fetched, via the <code>includeAttributes</code> parameter.<br />
     * Otherwise, this is <code>null</code>.
     */
    @JsonIgnore
    public UUID getId() {
        return of(attributes)
                .map(attr -> attr.get(ID))
                .map(UUID::fromString)
                .orElse(null);
    }

    /**
     * The {@link AccessRightReason} of why this access right exists. Only present when the extra attributes are fetched, via the <code>includeAttributes</code> parameter.<br />
     * Otherwise, this is <code>null</code>.
     */
    @JsonIgnore
    public AccessRightReason getReason() {
        return of(attributes)
                .map(attr -> attr.get(REASON))
                .map(AccessRightReason::findByName)
                .orElse(null);
    }

    /**
     * When an access right is granted for a specific parking spot, this value represents its unique identifier. It is null otherwise.
     */
    @JsonIgnore
    public UUID getParkingSpotId() {
        return of(attributes)
                .map(attr -> attr.get(PARKING_SPOT_ID))
                .map(UUID::fromString)
                .orElse(null);
    }

    /**
     * When an access right is granted for a specific parking spot, this value represents its name as defined by the managers. It is null otherwise.
     */
    @JsonIgnore
    public String getParkingSpotName() {
        return of(attributes)
                .map(attr -> attr.get(PARKING_SPOT_NAME))
                .orElse(null);
    }

    /**
     * When an access right is granted for a specific parking spot, this value represents its display name as it is shown to the end-users. It is null otherwise.
     */
    @JsonIgnore
    public String getParkingSpotDisplayName() {
        return of(attributes)
                .map(attr -> attr.get(PARKING_SPOT_DISPLAY_NAME))
                .orElse(null);
    }

    /**
     * When an access right is granted for a specific parking spot, this value represents the ID of the "Parking Site" (virtual, not physical) where it is defined in Commuty.
     */
    @JsonIgnore
    public UUID getParkingSpotZoneId() {
        return of(attributes)
                .map(attr -> attr.get(PARKING_SPOT_ZONE_ID))
                .map(UUID::fromString)
                .orElse(null);
    }

    /**
     * <code>true</code> when this access right is granted for a visitor. <code>false</code> otherwise.
     * The boolean value is <code>null</code> when this attribute is not requested.
     */
    @JsonIgnore
    public Boolean isVisitor() {
        return of(attributes)
                .map(attr -> attr.get(IS_VISITOR))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    /**
     * @return A unique identifier identifying the actual "subject" whose the access right belongs to. This can be the ID of a user, a visitor or an access exception.
     */
    @JsonIgnore
    public UUID getSubjectId() {
        return of(attributes)
                .map(attr -> attr.get(SUBJECT_ID))
                .map(UUID::fromString)
                .orElse(null);
    }

    /**
     * @return A label describing the "subject" whose the access right belongs to. This is usually the full name of a user, a visitor or an access exception.
     */
    @JsonIgnore
    public String getSubjectLabel() {
        return of(attributes)
                .map(attr -> attr.get(SUBJECT_LABEL))
                .orElse(null);
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

    /**
     * Check if this access right happens in the given moment or not.
     * @param moment The moment to check.
     * @return <code>true</code> if the moment given is during the access right, <code>false</code> otherwise.
     */
    public boolean covers(LocalDateTime moment) {
        if (moment == null) {
            throw new IllegalArgumentException("AccessRight.covers: moment must not be null");
        }
        return moment.isBefore(endTime.toLocalDateTime()) &&
                !moment.isBefore(startTime.toLocalDateTime());
    }

    /**
     * Check if this access right's start time is after the given moment.
     * @param moment The moment to check.
     * @return <code>true</code> if the given moment is after the start time, <code>false</code> otherwise.
     */
    public boolean startsAfter(LocalDateTime moment) {
        return startTime.toLocalDateTime().isAfter(moment);
    }
}
