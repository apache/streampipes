package de.fzi.cep.sepa.flink.samples.count.aggregate;

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
