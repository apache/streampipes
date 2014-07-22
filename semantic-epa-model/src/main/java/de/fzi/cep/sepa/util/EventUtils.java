package de.fzi.cep.sepa.util;

import java.util.UUID;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.StaticProperty;

public class EventUtils {

	public static URI createRandomUri(String ns)
	{
		return ValueFactoryImpl.getInstance().createURI(ns + UUID.randomUUID());
	}
	
	public static Gson getGson()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		//gsonBuilder.registerTypeAdapter(StaticProperty.class, new StaticPropertySerializer());
		//gsonBuilder.registerTypeAdapter(EventProperty.class, new EventPropertySerializer());
		gsonBuilder.setPrettyPrinting();
		Gson gson = gsonBuilder.create();
		return gson;
		
		
	}
	
}
