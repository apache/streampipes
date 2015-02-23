package de.fzi.cep.sepa.runtime.param;

import java.util.Map;

import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class EngineParameters<B extends BindingParameters> {

	private final Map<String, Map<String, Object>> inEventTypes;

	private final Map<String, Object> outEventType;

	private final B staticProperty;
	
	private final SEPAInvocationGraph graph;

	public EngineParameters(Map<String, Map<String, Object>> inEventTypes, Map<String, Object> outEventType,
		B staticProperty, SEPAInvocationGraph graph) {
		this.inEventTypes = inEventTypes;
		this.outEventType = outEventType;
		this.staticProperty = staticProperty;
		this.graph = graph;
	}

	public Map<String, Map<String, Object>> getInEventTypes() {
		return inEventTypes;
	}


	public Map<String, Object> getOutEventType() {
		return outEventType;
	}

	public B getStaticProperty() {
		return staticProperty;
	}

	public SEPAInvocationGraph getGraph() {
		return graph;
	}
	
	
}
