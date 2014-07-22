package de.fzi.cep.sepa.esper.filter.text;

import java.util.List;

import de.fzi.cep.sepa.esper.util.StringOperator;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class TextFilterParameter extends BindingParameters {

	private String keyword;
	private StringOperator stringOperator;
	private String filterProperty;
	
	public TextFilterParameter(String inName, String outName, List<String> allProperties, List<String> partitionProperties, String keyword, StringOperator stringOperator, String filterProperty) {
		super(inName, outName, allProperties, partitionProperties);
		this.inName = inName;
		this.outName = outName;
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
