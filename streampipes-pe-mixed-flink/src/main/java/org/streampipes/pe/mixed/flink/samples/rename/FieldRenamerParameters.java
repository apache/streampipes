package org.streampipes.pe.mixed.flink.samples.rename;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class FieldRenamerParameters extends EventProcessorBindingParams {

	private String oldPropertyName;
	private String newPropertyName;
	
	public FieldRenamerParameters(SepaInvocation graph, String oldPropertyName, String newPropertyName) {
		super(graph);
		this.oldPropertyName = oldPropertyName;
		this.newPropertyName = newPropertyName;
	}

	public String getOldPropertyName() {
		return oldPropertyName;
	}

	public void setOldPropertyName(String oldPropertyName) {
		this.oldPropertyName = oldPropertyName;
	}

	public String getNewPropertyName() {
		return newPropertyName;
	}

	public void setNewPropertyName(String newPropertyName) {
		this.newPropertyName = newPropertyName;
	}

	
}
