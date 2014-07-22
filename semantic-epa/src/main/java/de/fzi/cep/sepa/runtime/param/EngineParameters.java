package de.fzi.cep.sepa.runtime.param;

import java.util.Map;

public class EngineParameters<B extends BindingParameters> {

	private final Map<String, Map<String, Class<?>>> inEventTypes;

	private final OutputStrategy outEventType;

	private final B staticProperty;

	public EngineParameters(Map<String, Map<String, Class<?>>> inEventTypes, OutputStrategy outEventType,
		B staticProperty) {
		this.inEventTypes = inEventTypes;
		this.outEventType = outEventType;
		this.staticProperty = staticProperty;
	}

	public Map<String, Map<String, Class<?>>> getInEventTypes() {
		return inEventTypes;
	}

	public String getOutEventName() {
		return outEventType.getName();
	}

	public Map<String, Class<?>> getOutEventType() {
		return outEventType.buildOutEventType(inEventTypes);
	}

	public B getStaticProperty() {
		return staticProperty;
	}
}
