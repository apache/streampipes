package de.fzi.cep.sepa.flink.samples.wordcount;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class WordCountParameters extends BindingParameters {

	public WordCountParameters(SepaInvocation graph)
	{
		super(graph);
	}
}
