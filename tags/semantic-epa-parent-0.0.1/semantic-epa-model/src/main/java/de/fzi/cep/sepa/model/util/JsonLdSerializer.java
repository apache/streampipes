package de.fzi.cep.sepa.model.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fzi.cep.sepa.model.AbstractSEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public class JsonLdSerializer<T> implements JsonDeserializer<T>, JsonSerializer<T>{

	@Override
	public T deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		
		JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
 
        try {
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
	}

	@Override
	public JsonElement serialize(T src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		System.out.println(src.getClass().getCanonicalName());
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("properties", context.serialize(src, src.getClass()));
 
        return result;
	}

}
