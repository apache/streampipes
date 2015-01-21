package de.fzi.cep.sepa.esper.filter.numerical;

import java.util.List;

import de.fzi.cep.sepa.esper.util.NumericalOperator;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class NumericalFilterParameter extends BindingParameters {

	private int threshold;
	private NumericalOperator numericalOperator;
	private String filterProperty;
	
	public NumericalFilterParameter(String inName, String outName, List<String> allProperties, List<String> partitionProperties, int threshold, NumericalOperator numericalOperator, String filterProperty) {
		super(inName, outName, allProperties, partitionProperties);
		this.inName = inName;
		this.outName = outName;
		this.threshold = threshold;
		this.numericalOperator = numericalOperator;
		this.filterProperty = filterProperty;
	}

	
	public int getThreshold() {
		return threshold;
	}

	public NumericalOperator getNumericalOperator() {
		return numericalOperator;
	}



	public String getFilterProperty() {
		return filterProperty;
	}
	
	
	
}
