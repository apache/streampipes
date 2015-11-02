package de.fzi.cep.sepa.flink.samples.wordcount;

import java.io.Serializable;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;

public class WordCountTopology extends FlinkSepaRuntime<WordCountParameters, Tuple2<String, Integer>> implements Serializable {

	public WordCountTopology(WordCountParameters params)
	{
		super(params);
	}
	
	public WordCountTopology(WordCountParameters params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}
	
	protected DataStream<Tuple2<String, Integer>> getApplicationLogic(DataStream<String> messageStream)
	{
		return messageStream.flatMap(new JsonExtractor()).groupBy(0).sum(1);
	}

	@Override
	protected SerializationSchema<Tuple2<String, Integer>, byte[]> getKafkaSerializer() {
		return new SimpleKafkaSerializer();
	}

	@Override
	protected SerializationSchema<Tuple2<String, Integer>, String> getJmsSerializer() {
		return new SimpleJmsSerializer();
	}
		
}
