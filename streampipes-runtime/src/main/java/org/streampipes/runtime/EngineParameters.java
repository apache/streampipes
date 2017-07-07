package org.streampipes.runtime;

import java.util.Map;

import org.streampipes.model.impl.graph.SepaInvocation;

public class EngineParameters<B extends BindingParameters> {

	private final Map<String, Map<String, Object>> inEventTypes;

	private final Map<String, Object> outEventType;

	private final B staticProperty;
	
	private final SepaInvocation graph;

	public EngineParameters(Map<String, Map<String, Object>> inEventTypes, Map<String, Object> outEventType,
		B staticProperty, SepaInvocation graph) {
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

	public SepaInvocation getGraph() {
		return graph;
	}
	
	
}
