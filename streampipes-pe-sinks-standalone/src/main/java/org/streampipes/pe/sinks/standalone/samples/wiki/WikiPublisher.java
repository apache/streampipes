package org.streampipes.pe.sinks.standalone.samples.wiki;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.messaging.kafka.SpKafkaProducer;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class WikiPublisher implements EventSink<WikiParameters> {

	private JsonDataFormatDefinition jsonConverter;
	private SpKafkaProducer producer;

	private static final String topic = "org.streampipes.kt2017.wiki";
	private static final String kafkaHost = "ipe-koi15.fzi.de";
	private static final Integer kafkaPort = 9092;

	public WikiPublisher()
	{
		this.jsonConverter = new JsonDataFormatDefinition();
	}

	@Override
	public void bind(WikiParameters parameters) throws SpRuntimeException {
		this.producer = new SpKafkaProducer(kafkaHost + ":" +kafkaPort, topic);
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		JsonObject newObj = null;
		try {
			newObj = new JsonParser().parse(new String(jsonConverter.fromMap(event))).getAsJsonObject();
			JsonElement timestamp = newObj.get("timestamp");
			JsonElement locations = newObj.get("message");

			System.out.println("timestamp: " + timestamp.toString());
			System.out.println("locationList: " + locations.toString());

//		producer.publish(timestamp.toString() + "," + locations);
			producer.publish(locations.toString().substring(0, locations.toString().lastIndexOf(",")).getBytes());
		} catch (SpRuntimeException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void discard() throws SpRuntimeException {
		this.producer.disconnect();
	}
}
