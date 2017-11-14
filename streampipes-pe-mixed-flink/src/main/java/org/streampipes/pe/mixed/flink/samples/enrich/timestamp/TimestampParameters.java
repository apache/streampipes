package org.streampipes.pe.mixed.flink.samples.enrich.timestamp;

import java.util.List;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class TimestampParameters extends EventProcessorBindingParams {

	private String appendTimePropertyName;
	private List<String> selectProperties;
	
	public TimestampParameters(DataProcessorInvocation graph,
			String appendTimePropertyName, List<String> selectProperties) {
		super(graph);
		this.appendTimePropertyName = appendTimePropertyName;
		this.selectProperties = selectProperties;
	}

	public String getAppendTimePropertyName() {
		return appendTimePropertyName;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}

}
