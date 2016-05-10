package de.fzi.cep.sepa.esper.enrich.fixed;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class StaticValueEnricherParameters extends BindingParameters {

	private String appendPropertyName;
	private String value;
	
	public StaticValueEnricherParameters(SepaInvocation graph, String appendPropertyName, String value) {
		super(graph);
		this.appendPropertyName = appendPropertyName;
		this.value = value;
	}

	public String getAppendPropertyName() {
		return appendPropertyName;
	}

	public void setAppendPropertyName(String appendPropertyName) {
		this.appendPropertyName = appendPropertyName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

