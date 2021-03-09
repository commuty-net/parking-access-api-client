package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This holds the message sent to a webhook.
 * This is basically an operation (create/remove) on a granted access right.
 * This class can be used to marshall/unmarshall webhook messages.
 */
public class WebhookMessage {

    /**
     * The id of the access right, which could also be part of the access right's attributes.
     */
    private final UUID id;

    /**
     * The access right that has been removed or created.
     * Messages only manage access rights with granted=true.
     */
    private final AccessRight accessRight;

    /**
     * Whether the access right has been removed or created.
     */
    private final WebhookMessageOperation operation;

    @JsonCreator
    public WebhookMessage(@JsonProperty("id") UUID id,
                          @JsonProperty("accessRight") AccessRight accessRight,
                          @JsonProperty("operation") WebhookMessageOperation operation) {
        this.id = id;
        this.accessRight = accessRight;
        this.operation = operation;
    }

    @JsonProperty
    public UUID getId() {
        return id;
    }

    @JsonProperty
    public AccessRight getAccessRight() {
        return accessRight;
    }

    @JsonProperty
    public WebhookMessageOperation getOperation() {
        return operation;
    }
}
