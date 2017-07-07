package de.fzi.cep.sepa.flink.samples.enrich.timestamp;

import java.util.Map;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class TimestampEnricher implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

	private String appendTimePropertyName;
	
	public TimestampEnricher(String appendTimePropertyName) {
		this.appendTimePropertyName = appendTimePropertyName;
	}

	@Override
	public void flatMap(Map<String, Object> in,
			Collector<Map<String, Object>> out) throws Exception {
		in.put(appendTimePropertyName, System.currentTimeMillis());
		out.collect(in);
	}
	
	

}
