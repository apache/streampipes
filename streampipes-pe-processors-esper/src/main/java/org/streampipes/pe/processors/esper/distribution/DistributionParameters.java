package org.streampipes.pe.processors.esper.distribution;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class DistributionParameters extends EventProcessorBindingParams {

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
