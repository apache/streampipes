package de.fzi.cep.sepa.flink.samples.hasher;

import java.io.Serializable;
import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class FieldHasher implements Serializable, FlatMapFunction<Map<String, Object>, Map<String, Object>>{

	private HashAlgorithm hashAlgorithm;
	private String propertyName;
	
	public FieldHasher(String propertyName, HashAlgorithm hashAlgorithm) {
		this.propertyName = propertyName;
		this.hashAlgorithm = hashAlgorithm;
	}
	
	@Override
	public void flatMap(Map<String, Object> in,
			Collector<Map<String, Object>> out) throws Exception {
		in.put(propertyName, hashAlgorithm.toHashValue(in.get(propertyName)));
		out.collect(in);
	}

}
