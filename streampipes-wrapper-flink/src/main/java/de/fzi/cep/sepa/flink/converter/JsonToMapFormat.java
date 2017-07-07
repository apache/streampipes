package de.fzi.cep.sepa.flink.converter;

import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonToMapFormat implements FlatMapFunction<String, Map<String, Object>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ObjectMapper mapper;
	
	public JsonToMapFormat() {
		this.mapper = new ObjectMapper();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void flatMap(String in, Collector<Map<String, Object>> out)
			throws Exception {
		out.collect(mapper.readValue(in, HashMap.class));
		
	}

}
