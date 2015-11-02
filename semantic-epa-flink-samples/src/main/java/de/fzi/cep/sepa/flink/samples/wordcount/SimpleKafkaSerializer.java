package de.fzi.cep.sepa.flink.samples.wordcount;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

public class SimpleKafkaSerializer implements SerializationSchema<Tuple2<String, Integer>, byte[]>{

	@Override
	public byte[] serialize(Tuple2<String, Integer> element) {
		return ("{\"word:\" : \"" +element.f0 +"\",\"count\" : " +element.f1 +"}").getBytes();
	}

}
