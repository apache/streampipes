package de.fzi.cep.sepa.rest.util;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.fzi.cep.sepa.model.client.input.FormInput;


public class FormInputSerializer  implements JsonSerializer<FormInput>, JsonDeserializer<FormInput>{
	   
	    public JsonElement serialize(FormInput src, Type typeOfSrc, JsonSerializationContext context) {
	        
	    	JsonObject result = new JsonObject();
	        result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
	        result.add("properties", context.serialize(src, src.getClass()));
	 
	        return result;
	    }
	 
	    public FormInput deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
	        throws JsonParseException {
	       
	    	JsonObject jsonObject = json.getAsJsonObject();
	        String type = jsonObject.get("type").getAsString();
	        JsonElement element = jsonObject.get("properties");
	 
	        try {
	            return context.deserialize(element, Class.forName("de.fzi.cep.sepa.model.client.input." + type));
	        } catch (ClassNotFoundException cnfe) {
	            throw new JsonParseException("Unknown element type: " + type, cnfe);
	        }
	    }

	}

