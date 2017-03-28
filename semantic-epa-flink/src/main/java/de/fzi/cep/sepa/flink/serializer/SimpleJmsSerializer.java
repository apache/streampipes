package de.fzi.cep.sepa.flink.serializer;

import java.util.HashMap;
import java.util.Map;

import org.apache.flink.streaming.util.serialization.SerializationSchema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	
	public static void main(String[] args) {
		SimpleJmsSerializer serializer = new SimpleJmsSerializer();
		long current = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<>();
		map.put("key", 1);
		map.put("2ndkey", 2);
		for(int i = 0; i<= 1000000; i++) {
			
			serializer.serialize(map);
		}
		long now = System.currentTimeMillis();
		System.out.println("Took, " +(now-current) +", this is " +10000/((now-current)*1000));
	}

}
