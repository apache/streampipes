package de.fzi.cep.sepa.flink.serializer;

import java.util.Map;

import org.apache.flink.streaming.util.serialization.SerializationSchema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleJmsSerializer  implements SerializationSchema<Map<String, Object>, String>{

	private ObjectMapper objectMapper;
	
	public SimpleJmsSerializer() {
		this.objectMapper = new ObjectMapper();
	}
	
	@Override
	public String serialize(Map<String, Object> payload) {
		try {
			return objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
