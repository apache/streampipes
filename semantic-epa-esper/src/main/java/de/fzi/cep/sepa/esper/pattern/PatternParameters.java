package de.fzi.cep.sepa.esper.pattern;


import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class PatternParameters extends BindingParameters {

	private SepaInvocation graph;
	
	public PatternParameters(SepaInvocation graph) {
		super(graph);
		this.graph = graph;
	}

	public SepaInvocation getGraph() {
		return graph;
	}

	public void setGraph(SepaInvocation graph) {
		this.graph = graph;
	}
	
	

}
