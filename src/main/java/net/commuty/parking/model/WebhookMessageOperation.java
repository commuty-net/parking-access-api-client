package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Whether the access right has been removed or created.
 */
public enum WebhookMessageOperation {
    @JsonProperty("create")
    CREATE,
    @JsonProperty("remove")
    REMOVE,
    /**
     * Only used when parsed from a unknown object key.
     */
    @JsonEnumDefaultValue
    @JsonProperty("unknown")
    UNKNOWN
}
