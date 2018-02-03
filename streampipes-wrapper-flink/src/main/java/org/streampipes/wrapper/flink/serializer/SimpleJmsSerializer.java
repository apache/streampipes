package org.streampipes.wrapper.flink.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

import java.util.Map;

public class SimpleJmsSerializer  implements SerializationSchema<Map<String, Object>>{

	private ObjectMapper objectMapper;
	
	public SimpleJmsSerializer() {
		this.objectMapper = new ObjectMapper();
	}
	
	@Override
	public byte[] serialize(Map<String, Object> payload) {
		try {
			return objectMapper.writeValueAsBytes(payload);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}

}
