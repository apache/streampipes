package org.streampipes.pe.mixed.flink.samples.wordcount;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class WordCountParameters extends EventProcessorBindingParams {

	public WordCountParameters(DataProcessorInvocation graph)
	{
		super(graph);
	}
}
