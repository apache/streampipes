package org.streampipes.processors.aggregation.flink.processor.count;

public enum TimeScale {
	MINUTES("minutes"), SECONDS("seconds"), HOURS("hours");

	private String value;
	
	TimeScale(String value)
	{
		this.value = value;
	}
	
	public String value()
	{
		return value;
	}
}
