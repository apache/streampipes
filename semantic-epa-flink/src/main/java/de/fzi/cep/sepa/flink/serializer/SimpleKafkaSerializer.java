package de.fzi.cep.sepa.flink.serializer;

import java.util.Map;

import org.apache.flink.streaming.util.serialization.SerializationSchema;

public class SimpleKafkaSerializer implements SerializationSchema<Map<String, Object>>{

	SimpleJmsSerializer serializer;
	
	public SimpleKafkaSerializer() {
		this.serializer = new SimpleJmsSerializer();
	}
	
	@Override
	public byte[] serialize(Map<String, Object> element) {
		return serializer.serialize(element);
	}

}
