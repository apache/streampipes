package de.fzi.cep.sepa.model.util.serializer;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fzi.cep.sepa.model.impl.EventProperty;

public class EventPropertySerializer implements JsonSerializer<EventProperty>, JsonDeserializer<EventProperty> {
	   
    public JsonElement serialize(EventProperty src, Type typeOfSrc, JsonSerializationContext context) {
        
    	JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
       // result.add("properties", context.serialize(src, src.getClass()));

        return result;
    }
 
    public EventProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
       
    	JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
 
        try {
            return context.deserialize(element, Class.forName("java.lang." + type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }

}
