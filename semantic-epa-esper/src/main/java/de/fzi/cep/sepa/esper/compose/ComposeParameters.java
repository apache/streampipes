package de.fzi.cep.sepa.esper.compose;

import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class ComposeParameters extends BindingParameters{

	public ComposeParameters(SEPAInvocationGraph graph) {
		super(graph);
	}

}
