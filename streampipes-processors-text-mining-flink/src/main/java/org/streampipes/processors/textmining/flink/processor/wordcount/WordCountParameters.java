package org.streampipes.processors.textmining.flink.processor.wordcount;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class WordCountParameters extends EventProcessorBindingParams {

	private String wordCountFieldName;
	private Integer timeWindowValue;

	public WordCountParameters(DataProcessorInvocation graph, String wordCountFieldName, Integer timeWindowValue)
	{
		super(graph);
		this.wordCountFieldName = wordCountFieldName;
		this.timeWindowValue = timeWindowValue;
	}

	public String getWordCountFieldName() {
		return wordCountFieldName;
	}

	public Integer getTimeWindowValue() {
		return timeWindowValue;
	}
}
