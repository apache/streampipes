package de.fzi.cep.sepa.flink.samples.count.aggregate;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.Map;

public class CountAggregateEnricher implements FoldFunction<Tuple2<String, Map<String, Object>>, Map<String, Object>> {

	private String appendTimePropertyName;
	
	public CountAggregateEnricher(String appendTimePropertyName) {
		this.appendTimePropertyName = appendTimePropertyName;
	}


	@Override
	public Map<String, Object> fold(Map<String, Object> accumulator, Tuple2<String, Map<String, Object>> value) throws Exception {
		return null;
	}
}
