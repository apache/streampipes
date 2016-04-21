package de.fzi.cep.sepa.component.main.algorithm.langdetect;


import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class LanguageDetectionParameters extends BindingParameters{

	private String mappingPropertyName;
	
	public LanguageDetectionParameters(SepaInvocation graph, String mappingPropertyName) {
		super(graph);
		this.mappingPropertyName = mappingPropertyName;
	}

	public String getMappingPropertyName() {
		return mappingPropertyName;
	}

	public void setMappingPropertyName(String mappingPropertyName) {
		this.mappingPropertyName = mappingPropertyName;
	}
}
