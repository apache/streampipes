package org.streampipes.pe.processors.esper.compose;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class ComposeParameters extends EventProcessorBindingParams {

	public ComposeParameters(SepaInvocation graph) {
		super(graph);
	}

}
