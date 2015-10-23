package de.fzi.cep.sepa.esper.filter.numerical;

import de.fzi.cep.sepa.esper.util.NumericalOperator;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class NumericalFilterParameter extends BindingParameters {

	private double threshold;
	private NumericalOperator numericalOperator;
	private String filterProperty;
	
	public NumericalFilterParameter(SepaInvocation graph, double threshold, NumericalOperator numericalOperator, String filterProperty) {
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
