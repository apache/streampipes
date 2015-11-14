package de.fzi.cep.sepa.runtime.flat.datatype.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.fzi.cep.sepa.runtime.flat.datatype.DatatypeDefinition;

public class JsonDatatypeDefinition implements DatatypeDefinition {

	ObjectMapper objectMapper;
	
	public JsonDatatypeDefinition() {
		objectMapper = new ObjectMapper();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> unmarshal(String input) {
		try {
			return objectMapper.readValue(input, HashMap.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String marshal(Object event) {
		try {
			return objectMapper.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
