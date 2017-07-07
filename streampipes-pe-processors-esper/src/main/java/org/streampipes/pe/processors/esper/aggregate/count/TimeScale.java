package org.streampipes.pe.processors.esper.aggregate.count;

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
