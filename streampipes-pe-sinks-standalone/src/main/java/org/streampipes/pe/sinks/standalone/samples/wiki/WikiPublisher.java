package org.streampipes.pe.sinks.standalone.samples.wiki;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.EventProducer;

public class WikiPublisher implements EventListener<byte[]> {

	private EventProducer producer;

	public WikiPublisher(EventProducer producer)
	{
		this.producer = producer;
	}
	
	@Override
	public void onEvent(byte[] message) {
		JsonObject newObj = new JsonParser().parse(message.toString()).getAsJsonObject();
		JsonElement timestamp = newObj.get("timestamp");
		JsonElement locations = newObj.get("locationList");

		System.out.println("timestamp: " + timestamp.toString());
		System.out.println("locationList: " + locations.toString());

		producer.publish(timestamp.toString() + "," + locations);
	}

}
