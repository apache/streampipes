package de.fzi.cep.sepa.esper.aggregate.avg;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class AggregationParameter extends BindingParameters {

	private AggregationType aggregationType;
	private int outputEvery;
	private String groupBy;
	private int timeWindowSize;
	
	public AggregationParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, AggregationType aggregationType, int outputEvery, String groupBy, int timeWindowSize) {
		super(inName, outName, allProperties, partitionProperties);
		this.aggregationType = aggregationType;
		this.outputEvery = outputEvery;
		this.groupBy = groupBy;
		this.timeWindowSize = timeWindowSize;
	}

	public AggregationType getAggregationType() {
		return aggregationType;
	}

	public void setAggregationType(AggregationType aggregationType) {
		this.aggregationType = aggregationType;
	}

	public int getOutputEvery() {
		return outputEvery;
	}

	public void setOutputEvery(int outputEvery) {
		this.outputEvery = outputEvery;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public int getTimeWindowSize() {
		return timeWindowSize;
	}

	public void setTimeWindowSize(int timeWindowSize) {
		this.timeWindowSize = timeWindowSize;
	}	

}
