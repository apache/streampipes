package org.streampipes.processors.pattern.detection.flink.processor.and;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.List;

public class AndParameters extends EventProcessorBindingParams {

	private String timeUnit;
	private String matchingOperator;
	private int duration;
	private List<String> matchingProperties;
	
	
	public AndParameters(DataProcessorInvocation invocationGraph, String timeUnit,
                       String matchingOperator, int duration, List<String> matchingProperties) {
		super(invocationGraph);
		this.timeUnit = timeUnit;
		this.matchingOperator = matchingOperator;
		this.duration = duration;
		this.matchingProperties = matchingProperties;
	}


	public String getTimeUnit() {
		return timeUnit;
	}


	public String getMatchingOperator() {
		return matchingOperator;
	}


	public int getDuration() {
		return duration;
	}


	public List<String> getMatchingProperties() {
		return matchingProperties;
	}
	
	

}
