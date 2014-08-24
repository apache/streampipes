package de.fzi.cep.sepa.rest.util;

import com.google.gson.Gson;

public class Utils {

	public static Gson getGson()
	{
		Gson gson = new Gson();
		return gson;
	}
	
}
