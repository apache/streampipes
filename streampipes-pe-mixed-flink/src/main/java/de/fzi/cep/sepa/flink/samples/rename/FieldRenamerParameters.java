package de.fzi.cep.sepa.flink.samples.rename;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class FieldRenamerParameters extends BindingParameters {

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
