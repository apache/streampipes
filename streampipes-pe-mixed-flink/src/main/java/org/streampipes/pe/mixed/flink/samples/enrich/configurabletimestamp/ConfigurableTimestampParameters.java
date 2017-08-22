package org.streampipes.pe.mixed.flink.samples.enrich.configurabletimestamp;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

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
