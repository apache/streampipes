package org.streampipes.pe.processors.standalone.languagedetection;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class LanguageDetectionParameters extends EventProcessorBindingParams {

	private String mappingPropertyName;
	
	public LanguageDetectionParameters(DataProcessorInvocation graph, String mappingPropertyName) {
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
