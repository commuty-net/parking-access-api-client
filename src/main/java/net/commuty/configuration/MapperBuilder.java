package net.commuty.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MapperBuilder {


    private MapperBuilder() {

    }


    public static Gson defaultMapper() {
        return new GsonBuilder().create();
    }
}
