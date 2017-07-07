package org.streampipes.pe.algorithms.esper.filter.advancedtextfilter;

import java.util.List;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.BindingParameters;

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
