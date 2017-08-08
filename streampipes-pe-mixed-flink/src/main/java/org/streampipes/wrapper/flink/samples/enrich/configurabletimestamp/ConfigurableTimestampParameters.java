package org.streampipes.wrapper.flink.samples.enrich.configurabletimestamp;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.BindingParameters;

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
