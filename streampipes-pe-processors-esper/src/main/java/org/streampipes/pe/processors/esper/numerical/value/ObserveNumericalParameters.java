package org.streampipes.pe.processors.esper.numerical.value;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class ObserveNumericalParameters extends EventProcessorBindingParams {

	private String valueLimit;
	private double threshold;
	private String number;
	private String outputProperty;
	
	public ObserveNumericalParameters(DataProcessorInvocation graph, String valueLimit, double threshold, String number, String outputProperty) {
		super(graph);
		this.valueLimit = valueLimit;
		this.threshold = threshold;
		this.number = number;
		this.outputProperty = outputProperty;
	}

	public String getValueLimit() {
		return valueLimit;
	}

	public double getThreshold() {
		return threshold;
	}

	public String getNumber() {
		return number;
	}

	public String getOutputProperty() {
		return outputProperty;
	}
	
}
