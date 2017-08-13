package org.streampipes.wrapper.flink.samples.wordcount;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class WordCountParameters extends EventProcessorBindingParams {

	public WordCountParameters(SepaInvocation graph)
	{
		super(graph);
	}
}
