package de.fzi.cep.sepa.flink.samples.enrich.configurabletimestamp;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

import java.util.List;

public class ConfigurableTimestampParameters extends BindingParameters {

	private String appendTimePropertyName;

	public ConfigurableTimestampParameters(SepaInvocation graph,
                                           String appendTimePropertyName) {
		super(graph);
		this.appendTimePropertyName = appendTimePropertyName;
	}

	public String getAppendTimePropertyName() {
		return appendTimePropertyName;
	}


}
