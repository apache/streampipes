package de.fzi.cep.sepa.esper.filter.text;

import de.fzi.cep.sepa.esper.util.StringOperator;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class TextFilterParameter extends BindingParameters {

	private String keyword;
	private StringOperator stringOperator;
	private String filterProperty;
	
	public TextFilterParameter(SepaInvocation graph, String keyword, StringOperator stringOperator, String filterProperty) {
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
