package org.streampipes.serializers.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.streampipes.model.client.messages.Message;
import org.streampipes.model.client.ontology.Range;


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
		return gsonBuilder;	
	}
	
}
