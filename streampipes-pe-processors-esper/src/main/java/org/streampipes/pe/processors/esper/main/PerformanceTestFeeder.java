//package org.streampipes.pe.processors.esper.main;
//
//import com.espertech.esper.client.EPRuntime;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.streampipes.commons.exceptions.SpRuntimeException;
//import org.streampipes.messaging.EventConsumer;
//import org.streampipes.messaging.InternalEventProcessor;
//import org.streampipes.messaging.kafka.SpKafkaConsumer;
//
//public class PerformanceTestFeeder implements EventConsumer<byte[]>, Runnable {
//
//	private String zookeeperHost;
//	private int zookeeperPort;
//	private String topic;
//	private EPRuntime runtime;
//	private JsonParser parser;
//
//	private SpKafkaConsumer kafkaConsumerGroup;
//
//	public PerformanceTestFeeder(String zookeeperHost, int zookeeperPort, String topic, EPRuntime runtime) {
//		this.zookeeperHost = zookeeperHost;
//		this.zookeeperPort = zookeeperPort;
//		this.topic = topic;
//		this.runtime = runtime;
//		this.parser = new JsonParser();
//	}
//
//	@Override
//	public void onEvent(byte[] payload) {
//		//System.out.println("event");
//		runtime.sendEvent(toObj(payload));
//	}
//
//	private RandomNumberEvent toObj(byte[] payload) {
//		String json = new String(payload);
//
//		JsonElement element = parser.parse(json);
//		JsonObject object = element.getAsJsonObject();
//		return new RandomNumberEvent(object.get("timestamp").getAsLong(),
//				object.get("randomValue").getAsInt(),
//				object.get("randomString").getAsString(),
//				object.get("count").getAsLong());
//
//	}
//
//	@Override
//	public void run() {
//		// TODO fix later
//		//kafkaConsumerGroup = new SpKafkaConsumer(zookeeperHost +":" +zookeeperPort,
//		//		topic, this);
//		//Thread thread = new Thread(kafkaConsumerGroup);
//		//thread.start();
//	}
//
//	@Override
//	public void connect(byte[] protocolSettings, InternalEventProcessor<byte[]> eventProcessor) throws SpRuntimeException {
//
//	}
//
//	@Override
//	public void disconnect() throws SpRuntimeException {
//
//	}
//
//	@Override
//	public Boolean isConnected() {
//		return null;
//	}
//}
