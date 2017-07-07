package de.fzi.cep.sepa.esper.distribution;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

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
