package de.fzi.cep.sepa.esper.aggregate.avg;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class AggregationParameter extends BindingParameters {

	private AggregationType aggregationType;
	private int outputEvery;
	private int timeWindowSize;
	private String aggregate;
	private List<String> groupBy;
	
	public AggregationParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties, AggregationType aggregationType, int outputEvery, List<String> groupBy, String aggregate, int timeWindowSize) {
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

	public List<String> getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(List<String> groupBy) {
		this.groupBy = groupBy;
	}

	public int getTimeWindowSize() {
		return timeWindowSize;
	}

	public void setTimeWindowSize(int timeWindowSize) {
		this.timeWindowSize = timeWindowSize;
	}

	public String getAggregate() {
		return aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}	
	
	

}
