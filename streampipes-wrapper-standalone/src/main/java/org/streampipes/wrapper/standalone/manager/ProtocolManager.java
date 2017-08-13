package org.streampipes.wrapper.standalone.manager;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.EventConsumer;
import org.streampipes.messaging.EventProducer;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.wrapper.standalone.datatype.DatatypeDefinition;
import org.streampipes.wrapper.standalone.protocol.Consumer;
import org.streampipes.wrapper.standalone.protocol.Producer;
import org.streampipes.wrapper.standalone.protocol.jms.JmsConsumer;
import org.streampipes.wrapper.standalone.protocol.jms.JmsProducer;
import org.streampipes.wrapper.standalone.protocol.kafka.KafkaConsumer;
import org.streampipes.wrapper.standalone.protocol.kafka.KafkaProducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolManager {

	public static Map<String, EventConsumer<?>> consumers = new HashMap<>();
	public static Map<String, EventProducer<?>> producers = new HashMap<>();
	
	private static final String topicPrefix = "topic://";
	
	private static Map<String, List<String>> consumerLeaderMap = new HashMap<>();

	
	public static void removeFromConsumerMap(String topic, String routeId) {
		consumerLeaderMap.get(topic).remove(routeId);
	}
	
	public static boolean isTopicLeader(String topic, String routeId) {
		return consumerLeaderMap.get(topic).get(0).equals(routeId);
	}
	
	public static EventConsumer<?> findConsumer(TransportProtocol protocol, String routeId) throws
					SpRuntimeException {
		
		if (consumers.containsKey(topicPrefix +topicName(protocol))) {
			consumerLeaderMap.get(topicPrefix +topicName(protocol)).add(routeId);
			return consumers.get(topicPrefix +topicName(protocol));
		}
		else {
			List<String> consumerList = new ArrayList<>();
			consumerList.add(routeId);
			consumerLeaderMap.put(topicPrefix +topicName(protocol), consumerList);
			return makeConsumer(protocol);
		}
		
	}
	
	private static EventConsumer<?> makeConsumer(TransportProtocol protocol) throws SpRuntimeException {
		return PManager.getConsumer(protocol);
	}

	public static EventProducer<?> findProducer(TransportProtocol protocol, TransportFormat format) throws SpRuntimeException {
		if (producers.containsKey(topicPrefix +topicName(protocol))) return producers.get(topicPrefix +topicName(protocol));
		else return makeProducer(protocol);
	}
	
	private static EventProducer<?> makeProducer(TransportProtocol protocol) throws SpRuntimeException {
		return PManager.getProducer(protocol);
	}

	private static String topicName(TransportProtocol protocol) {
		return protocol.getTopicName();
	}
	
	public static void removeProducer(String topicWithPrefix) {
		try {
			producers.get(topicWithPrefix).disconnect();
		} catch (SpRuntimeException e) {
			e.printStackTrace();
		}
		producers.remove(topicWithPrefix);
	}
	
	public static void removeConsumer(String topicWithPrefix) {
		if (consumers.get(topicWithPrefix).getCurrentListenerCount() == 0) {
			consumers.get(topicWithPrefix).closeConsumer();
			consumers.remove(topicWithPrefix);
		}
	}
	
}
