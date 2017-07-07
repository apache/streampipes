package org.streampipes.wrapper.flink.samples.wordcount;

import java.io.Serializable;
import java.util.Map;

import org.apache.flink.streaming.api.datastream.DataStream;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.streampipes.wrapper.flink.converter.ObjectToMapConverter;
import org.streampipes.model.util.SepaUtils;

public class WordCountProgram extends FlinkSepaRuntime<WordCountParameters> implements Serializable {

	public WordCountProgram(WordCountParameters params)
	{
		super(params);
	}
	
	public WordCountProgram(WordCountParameters params, FlinkDeploymentConfig config)
	{
		super(params, config);
	}
	
	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {
		
		String textMapping = SepaUtils.getMappingPropertyName(params.getGraph(), "text-mapping");
		return messageStream[0]
				.flatMap(new WordSplitter(textMapping))
				.keyBy("word")
				.sum("count")
				.flatMap(new ObjectToMapConverter<Word>());
	}
		
}
