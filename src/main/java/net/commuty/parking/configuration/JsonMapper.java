package net.commuty.parking.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.Reader;
import java.util.ServiceLoader;

public class JsonMapper {

    private final ObjectMapper internalMapper;

    private JsonMapper() {
        this.internalMapper = initMapper();
    }

    private ObjectMapper initMapper() {


        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModules(ServiceLoader.load(Module.class))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }


    public static JsonMapper create() {
        return new JsonMapper();
    }

    public <T> T read(Reader reader, Class<T> clazz) throws IOException {
        return internalMapper.readValue(reader, clazz);
    }

    public String write(Object body) throws IOException {
        return internalMapper.writeValueAsString(body);
    }
}