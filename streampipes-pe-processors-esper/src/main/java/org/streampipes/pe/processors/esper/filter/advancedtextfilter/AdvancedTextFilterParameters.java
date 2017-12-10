package org.streampipes.pe.processors.esper.filter.advancedtextfilter;

import java.util.List;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class AdvancedTextFilterParameters extends EventProcessorBindingParams {

	private String operation;
	private String propertyName;
	private List<String> keywords;
	
	public AdvancedTextFilterParameters(DataProcessorInvocation graph, String operation, String propertyName, List<String> keywords) {
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
