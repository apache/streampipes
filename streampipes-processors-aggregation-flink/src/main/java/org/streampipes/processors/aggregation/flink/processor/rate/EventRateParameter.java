package org.streampipes.processors.aggregation.flink.processor.rate;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class EventRateParameter extends EventProcessorBindingParams {

	int avgRate;
	int outputRate;
	String outputName; 
	
	public EventRateParameter(DataProcessorInvocation graph, int avgRate, int outputRate, String outputName) {
		super(graph);
		this.avgRate = avgRate;
		this.outputRate = outputRate;
		this.outputName = outputName;
	}

	public int getAvgRate() {
		return avgRate;
	}

	public void setAvgRate(int avgRate) {
		this.avgRate = avgRate;
	}

	public int getOutputRate() {
		return outputRate;
	}

	public void setOutputRate(int outputRate) {
		this.outputRate = outputRate;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}
	
	
	
}
