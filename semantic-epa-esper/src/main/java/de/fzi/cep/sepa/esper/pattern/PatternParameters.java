package de.fzi.cep.sepa.esper.pattern;


import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class PatternParameters extends BindingParameters {

	private SEPAInvocationGraph graph;
	
	public PatternParameters(SEPAInvocationGraph graph) {
		super(graph);
		this.graph = graph;
	}

	public SEPAInvocationGraph getGraph() {
		return graph;
	}

	public void setGraph(SEPAInvocationGraph graph) {
		this.graph = graph;
	}
	
	

}
