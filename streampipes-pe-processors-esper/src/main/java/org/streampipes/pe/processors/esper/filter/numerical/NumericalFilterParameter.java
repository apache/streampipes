package org.streampipes.pe.processors.esper.filter.numerical;

import org.streampipes.pe.processors.esper.util.NumericalOperator;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class NumericalFilterParameter extends EventProcessorBindingParams {

	private double threshold;
	private NumericalOperator numericalOperator;
	private String filterProperty;
	
	public NumericalFilterParameter(DataProcessorInvocation graph, double threshold, NumericalOperator numericalOperator, String filterProperty) {
		super(graph);
		this.threshold = threshold;
		this.numericalOperator = numericalOperator;
		this.filterProperty = filterProperty;
	}

	public double getThreshold() {
		return threshold;
	}

	public NumericalOperator getNumericalOperator() {
		return numericalOperator;
	}

	public String getFilterProperty() {
		return filterProperty;
	}
	
	
	
}
