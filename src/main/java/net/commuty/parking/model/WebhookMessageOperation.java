package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Whether the access right has been removed or created.
 */
public enum WebhookMessageOperation {
    @JsonProperty("create")
    CREATE,
    @JsonProperty("remove")
    REMOVE
}
