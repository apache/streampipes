package org.streampipes.pe.processors.esper.numerical.value;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

public class ObserveNumericalParameters extends BindingParameters {

	private String valueLimit;
	private double threshold;
	private String number;
	private String outputProperty;
	
	public ObserveNumericalParameters(SepaInvocation graph, String valueLimit, double threshold, String number, String outputProperty) {
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
