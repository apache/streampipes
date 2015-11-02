package de.fzi.cep.sepa.flink.samples.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.connectors.json.JSONParser;
import org.apache.flink.util.Collector;

public class JsonExtractor implements FlatMapFunction<String, Tuple2<String, Integer>>{

	@Override
	public void flatMap(String json, Collector<Tuple2<String, Integer>> out) throws Exception {	
		String location = new JSONParser(json).parse("location").getString("retValue");
		out.collect(new Tuple2<String, Integer>(location, 1));
	}

}
