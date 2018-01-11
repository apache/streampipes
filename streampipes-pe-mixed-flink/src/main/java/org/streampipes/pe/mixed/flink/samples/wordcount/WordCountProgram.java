package org.streampipes.pe.mixed.flink.samples.wordcount;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;
import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.converter.ObjectToMapConverter;

import java.io.Serializable;
import java.util.Map;

public class WordCountProgram extends FlinkDataProcessorRuntime<WordCountParameters> implements Serializable {

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
		
		String textMapping = SepaUtils.getMappingPropertyName(getGraph(), "text-mapping");
		return messageStream[0]
				.flatMap(new WordSplitter(textMapping))
				.keyBy("word")
				.sum("count")
				.flatMap(new ObjectToMapConverter<Word>());
	}
		
}
