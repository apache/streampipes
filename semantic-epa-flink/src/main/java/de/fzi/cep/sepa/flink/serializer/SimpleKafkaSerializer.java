package de.fzi.cep.sepa.flink.serializer;

import java.util.Map;

import org.apache.flink.streaming.util.serialization.SerializationSchema;

public class SimpleKafkaSerializer implements SerializationSchema<Map<String, Object>, byte[]>{

	@Override
	public byte[] serialize(Map<String, Object> element) {
		return new SimpleJmsSerializer().serialize(element).getBytes();
	}

}
