package de.fzi.cep.sepa.flink.samples.wordcount;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

public class SimpleJmsSerializer  implements SerializationSchema<Tuple2<String, Integer>, String>{

	@Override
	public String serialize(Tuple2<String, Integer> element) {
		return "{\"word\" : \"" +element.f0 +"\",\"count\" : " +element.f1 +"}";
	}

}
