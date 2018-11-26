package org.streampipes.processors.textmining.flink.processor.wordcount;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.streampipes.processors.textmining.flink.AbstractTextMiningProgram;
import org.streampipes.wrapper.flink.converter.ObjectToMapConverter;

import java.io.Serializable;
import java.util.Map;

public class WordCountProgram extends AbstractTextMiningProgram<WordCountParameters> implements Serializable {

	public WordCountProgram(WordCountParameters params, boolean debug) {
		super(params, debug);
	}

	public WordCountProgram(WordCountParameters params) {
		super(params);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {

		return messageStream[0]
				.flatMap(new WordSplitter(params.getWordCountFieldName()))
				.keyBy("word")
				.sum("count")
				.flatMap(new ObjectToMapConverter<>());
	}
		
}
