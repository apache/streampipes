package de.fzi.cep.sepa.esper.pattern;

import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class PatternParameters extends BindingParameters {

	private SEPAInvocationGraph graph;
	
	public PatternParameters(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, SEPAInvocationGraph graph) {
		super(inName, outName, allProperties, partitionProperties);
		this.graph = graph;
	}

	public SEPAInvocationGraph getGraph() {
		return graph;
	}

	public void setGraph(SEPAInvocationGraph graph) {
		this.graph = graph;
	}
	
	

}
