package de.fzi.cep.sepa.flink.samples.wordcount;

import java.io.Serializable;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.flink.converter.ObjectToMapConverter;
import de.fzi.cep.sepa.model.util.SepaUtils;

public class WordCountTopology extends FlinkSepaRuntime<WordCountParameters> implements Serializable {

	public WordCountTopology(WordCountParameters params)
	{
		super(params);
	}
	
	public WordCountTopology(WordCountParameters params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}
	
	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>> messageStream) {
		
		String textMapping = SepaUtils.getMappingPropertyName(params.getGraph(), "text-mapping");
		return messageStream
				.flatMap(new WordSplitter(textMapping))
				.keyBy("word")
				.sum("count")
				.flatMap(new ObjectToMapConverter<Word>());
	}
		
}
