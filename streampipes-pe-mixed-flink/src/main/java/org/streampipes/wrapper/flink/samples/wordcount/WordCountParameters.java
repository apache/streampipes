package org.streampipes.wrapper.flink.samples.wordcount;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

public class WordCountParameters extends BindingParameters {

	public WordCountParameters(SepaInvocation graph)
	{
		super(graph);
	}
}
