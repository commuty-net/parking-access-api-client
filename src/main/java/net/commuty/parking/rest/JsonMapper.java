package net.commuty.parking.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.commuty.parking.http.Error;
import net.commuty.parking.http.Mapper;

import java.io.IOException;
import java.io.InputStream;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.DeserializationFeature.*;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

class JsonMapper implements Mapper {

    private final ObjectMapper mapper;

    protected JsonMapper() {
        this.mapper = initMapper();
    }

    private ObjectMapper initMapper() {
        return new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModules(new JavaTimeModule())
                .setSerializationInclusion(NON_NULL)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .enable(READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
    }


    static JsonMapper create() {
        return new JsonMapper();
    }

    @Override
    public String write(Object body) throws JsonProcessingException {
        return mapper.writeValueAsString(body);
    }

    @Override
    public <R> R read(InputStream stream, Class<R> type) throws IOException {
        return mapper.readValue(stream, type);
    }

    @Override
    public Error readError(InputStream stream) throws IOException {
        return read(stream, JsonError.class);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
}
