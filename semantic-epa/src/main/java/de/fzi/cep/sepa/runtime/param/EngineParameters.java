package de.fzi.cep.sepa.runtime.param;

import java.util.Map;

import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class EngineParameters<B extends BindingParameters> {

	private final Map<String, Map<String, Class<?>>> inEventTypes;

	private final Map<String, Class<?>> outEventType;

	private final B staticProperty;
	
	private final SEPAInvocationGraph graph;

	public EngineParameters(Map<String, Map<String, Class<?>>> inEventTypes, Map<String, Class<?>> outEventType,
		B staticProperty, SEPAInvocationGraph graph) {
		this.inEventTypes = inEventTypes;
		this.outEventType = outEventType;
		this.staticProperty = staticProperty;
		this.graph = graph;
	}

	public Map<String, Map<String, Class<?>>> getInEventTypes() {
		return inEventTypes;
	}


	public Map<String, Class<?>> getOutEventType() {
		return outEventType;
	}

	public B getStaticProperty() {
		return staticProperty;
	}

	public SEPAInvocationGraph getGraph() {
		return graph;
	}
	
	
}
