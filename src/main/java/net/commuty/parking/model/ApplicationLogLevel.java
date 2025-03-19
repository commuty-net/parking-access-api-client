package net.commuty.parking.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum ApplicationLogLevel {
    @JsonProperty("info")
    INFO,
    @JsonProperty("warn")
    WARN,
    @JsonProperty("error")
    ERROR;
}
