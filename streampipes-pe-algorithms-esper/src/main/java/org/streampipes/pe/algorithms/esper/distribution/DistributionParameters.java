package org.streampipes.pe.algorithms.esper.distribution;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

public class DistributionParameters extends BindingParameters{

	private int timeWindow;
	private String mappingProperty;
	
	public DistributionParameters(SepaInvocation graph, int timeWindow, String mappingProperty) {
		super(graph);
		this.timeWindow = timeWindow;
		this.mappingProperty = mappingProperty;
	}

	public int getTimeWindow() {
		return timeWindow;
	}

	public String getMappingProperty() {
		return mappingProperty;
	}
}
