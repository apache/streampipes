package org.streampipes.processors.aggregation.flink.processor.count;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class CountParameters extends EventProcessorBindingParams {

	private int timeWindow;
	private List<String> groupBy;
	private TimeScale timeScale;
	private List<String> selectProperties;
	
	public CountParameters(DataProcessorInvocation graph, int timeWindow, List<String> groupBy, TimeScale timeScale, List<String> selectProperties) {
		super(graph);
		this.timeScale = timeScale;
		this.groupBy = groupBy;
		this.timeWindow = timeWindow;
		this.selectProperties = selectProperties;
	}

	public int getTimeWindow() {
		return timeWindow;
	}

	public List<String> getGroupBy() {
		return groupBy;
	}

	public TimeScale getTimeScale() {
		return timeScale;
	}

	public List<String> getSelectProperties() {
		return selectProperties;
	}
	
}