package de.fzi.sepa.model.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fzi.cep.sepa.model.client.ontology.Range;
import de.fzi.cep.sepa.model.util.GsonSerializer;


public class Utils {

	public static Gson getGson()
	{
		GsonBuilder gsonBuilder = getGsonBuilder();
		Gson gson = gsonBuilder.create();
		return gson;
	}
	
	public static GsonBuilder getGsonBuilder()
	{
		GsonBuilder gsonBuilder = GsonSerializer.getGsonBuilder();
		gsonBuilder.registerTypeAdapter(Range.class, new RangeSerializer());
		return gsonBuilder;	
	}
	
}
