package org.streampipes.pe.processors.esper.main;

import com.espertech.esper.client.EPRuntime;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.kafka.StreamPipesKafkaConsumer;

public class PerformanceTestFeeder implements EventConsumer<byte[]>, Runnable {

	private String zookeeperHost;
	private int zookeeperPort;
	private String topic;
	private EPRuntime runtime;
	private JsonParser parser;
	
	private StreamPipesKafkaConsumer kafkaConsumerGroup;
	
	public PerformanceTestFeeder(String zookeeperHost, int zookeeperPort, String topic, EPRuntime runtime) {
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
		this.topic = topic;
		this.runtime = runtime;
		this.parser = new JsonParser();
	}
	
	@Override
	public void onEvent(byte[] payload) {
		//System.out.println("event");
		runtime.sendEvent(toObj(payload));
	}

	private RandomNumberEvent toObj(byte[] payload) {
		String json = new String(payload);
		
		JsonElement element = parser.parse(json);
		JsonObject object = element.getAsJsonObject();
		return new RandomNumberEvent(object.get("timestamp").getAsLong(),
				object.get("randomValue").getAsInt(),
				object.get("randomString").getAsString(),
				object.get("count").getAsLong());
		
	}

	@Override
	public void run() {
		kafkaConsumerGroup = new StreamPipesKafkaConsumer(zookeeperHost +":" +zookeeperPort,
				topic, this);
		Thread thread = new Thread(kafkaConsumerGroup);
		thread.start();
	}

}
