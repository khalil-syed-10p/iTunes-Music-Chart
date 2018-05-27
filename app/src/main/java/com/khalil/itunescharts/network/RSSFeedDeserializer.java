package com.khalil.itunescharts.network;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class RSSFeedDeserializer<T> implements JsonDeserializer {

    private static final String KEY_FEED = "feed";
    private static final String KEY_RESULTS = "results";

    @Override
    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        JsonElement resultsElement = jsonElement.getAsJsonObject().get(KEY_FEED);
        if(resultsElement == null || resultsElement.isJsonNull()) {
            resultsElement = jsonElement;
        }

        GsonBuilder builder = new GsonBuilder();
        return builder.create().fromJson(resultsElement, type);
    }
}
