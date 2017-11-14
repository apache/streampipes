package org.streampipes.pe.mixed.flink.samples.enrich.configurabletimestamp;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class ConfigurableTimestampParameters extends EventProcessorBindingParams {

	private String appendTimePropertyName;

	public ConfigurableTimestampParameters(DataProcessorInvocation graph,
                                           String appendTimePropertyName) {
		super(graph);
		this.appendTimePropertyName = appendTimePropertyName;
	}

	public String getAppendTimePropertyName() {
		return appendTimePropertyName;
	}


}
