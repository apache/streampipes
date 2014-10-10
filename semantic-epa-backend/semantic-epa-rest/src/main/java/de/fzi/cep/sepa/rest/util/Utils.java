package de.fzi.cep.sepa.rest.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fzi.cep.sepa.model.client.input.FormInput;



public class Utils {

	public static Gson getGson()
	{
		GsonBuilder gsonBuilder = new com.google.gson.GsonBuilder();
		gsonBuilder.registerTypeAdapter(FormInput.class, new FormInputSerializer());
		gsonBuilder.setPrettyPrinting();
		Gson gson = gsonBuilder.create();
		return gson;
		
	}
	
}
