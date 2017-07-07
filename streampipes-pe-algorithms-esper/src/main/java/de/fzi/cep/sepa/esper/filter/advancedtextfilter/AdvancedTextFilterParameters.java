package de.fzi.cep.sepa.esper.filter.advancedtextfilter;

import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class AdvancedTextFilterParameters extends BindingParameters{

	private String operation;
	private String propertyName;
	private List<String> keywords;
	
	public AdvancedTextFilterParameters(SepaInvocation graph, String operation, String propertyName, List<String> keywords) {
		super(graph);
		this.operation = operation;
		this.propertyName = propertyName;
		this.keywords = keywords;
	}

	public String getOperation() {
		return operation;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public List<String> getKeywords() {
		return keywords;
	}
	
}
