package org.streampipes.wrapper.flink.samples.count.aggregate;

import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.List;

public class CountAggregateParameters extends EventProcessorBindingParams {

	private Time timeWindowSize;
	private Time slideWindowSize;
	private List<String> groupBy;

	public CountAggregateParameters(SepaInvocation graph, Time timeWindowSize, Time slideWindowSize
			, List<String> groupBy) {
		super(graph);
		this.groupBy = groupBy;
		this.timeWindowSize = timeWindowSize;
		this.slideWindowSize = slideWindowSize;
	}


	public List<String> getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(List<String> groupBy) {
		this.groupBy = groupBy;
	}

	public Time getTimeWindowSize() {
		return timeWindowSize;
	}

	public void setTimeWindowSize(Time timeWindowSize) {
		this.timeWindowSize = timeWindowSize;
	}

	public Time getSlideWindowSize() {
		return slideWindowSize;
	}

	public void setSlideWindowSize(Time slideWindowSize) {
		this.slideWindowSize = slideWindowSize;
	}
}
