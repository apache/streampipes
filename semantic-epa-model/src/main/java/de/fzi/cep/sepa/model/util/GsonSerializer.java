package de.fzi.cep.sepa.model.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.fzi.cep.sepa.model.AbstractSEPAElement;

public class GsonSerializer {

	public static Gson getGson()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(AbstractSEPAElement.class, new JsonLdSerializer());
		builder.setPrettyPrinting();
		return builder.create();
	}
}
