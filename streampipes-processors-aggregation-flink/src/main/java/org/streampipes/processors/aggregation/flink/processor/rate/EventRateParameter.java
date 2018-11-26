package org.streampipes.processors.aggregation.flink.processor.rate;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class EventRateParameter extends EventProcessorBindingParams {

	private Integer avgRate;
	
	public EventRateParameter(DataProcessorInvocation graph, int avgRate) {
		super(graph);
		this.avgRate = avgRate;
	}

	public int getAvgRate() {
		return avgRate;
	}

}
