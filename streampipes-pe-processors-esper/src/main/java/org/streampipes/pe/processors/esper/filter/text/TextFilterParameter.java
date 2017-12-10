package org.streampipes.pe.processors.esper.filter.text;

import org.streampipes.pe.processors.esper.util.StringOperator;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class TextFilterParameter extends EventProcessorBindingParams {

	private String keyword;
	private StringOperator stringOperator;
	private String filterProperty;
	
	public TextFilterParameter(DataProcessorInvocation graph, String keyword, StringOperator stringOperator, String filterProperty) {
		super(graph);
		this.keyword = keyword;
		this.stringOperator = stringOperator;
		this.filterProperty = filterProperty;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public StringOperator getStringOperator() {
		return stringOperator;
	}

	public void setStringOperator(StringOperator stringOperator) {
		this.stringOperator = stringOperator;
	}

	public String getFilterProperty() {
		return filterProperty;
	}
	
	
	
}
