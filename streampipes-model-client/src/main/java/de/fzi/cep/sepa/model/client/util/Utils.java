package de.fzi.cep.sepa.model.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fzi.cep.sepa.kpi.Operation;
import de.fzi.cep.sepa.model.client.messages.Message;
import de.fzi.cep.sepa.model.client.ontology.Range;
import de.fzi.cep.sepa.model.util.GsonSerializer;
import de.fzi.cep.sepa.model.util.JsonLdSerializer;


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
		gsonBuilder.registerTypeAdapter(Message.class, new JsonLdSerializer<Message>());
		gsonBuilder.registerTypeAdapter(Operation.class, new JsonLdSerializer<Operation>());
		return gsonBuilder;	
	}
	
}
