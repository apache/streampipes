package org.streampipes.pe.sinks.standalone.samples.wiki;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.streampipes.messaging.EventProducer;
import org.streampipes.messaging.InternalEventProcessor;

public class WikiPublisher implements InternalEventProcessor<byte[]> {

	private EventProducer producer;

	public WikiPublisher(EventProducer producer)
	{
		this.producer = producer;
	}
	
	@Override
	public void onEvent(byte[] message) {
		String m = new String(message);
		JsonObject newObj = new JsonParser().parse(m).getAsJsonObject();
		JsonElement timestamp = newObj.get("timestamp");
		JsonElement locations = newObj.get("message");

		System.out.println("timestamp: " + timestamp.toString());
		System.out.println("locationList: " + locations.toString());

//		producer.publish(timestamp.toString() + "," + locations);
		producer.publish(locations.toString().substring(0, locations.toString().lastIndexOf(",")).getBytes());
	}

}
