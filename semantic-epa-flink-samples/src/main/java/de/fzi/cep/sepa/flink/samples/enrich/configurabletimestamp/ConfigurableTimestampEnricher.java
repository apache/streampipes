package de.fzi.cep.sepa.flink.samples.enrich.configurabletimestamp;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.Map;

public class ConfigurableTimestampEnricher implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

	private String appendTimePropertyName;
	
	public ConfigurableTimestampEnricher(String appendTimePropertyName) {
		this.appendTimePropertyName = appendTimePropertyName;
	}

	@Override
	public void flatMap(Map<String, Object> in,
			Collector<Map<String, Object>> out) throws Exception {
		in.put(appendTimePropertyName, System.currentTimeMillis());
		out.collect(in);
	}
	
	

}
