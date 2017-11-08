package org.streampipes.serializers.json;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.net.URI;

/**
 * Created by riemer on 01.09.2016.
 */
public class UriSerializer implements JsonSerializer<URI>, JsonDeserializer<URI> {
    public JsonElement serialize(URI src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public URI deserialize(final JsonElement src, final Type srcType,
                           final JsonDeserializationContext context) throws JsonParseException {
        return URI.create(src.getAsString());
    }
}
