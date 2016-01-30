package de.fzi.proasense.demonstrator.config;

public enum DemonstratorVariables {

    FLOWRATESENSOR("flowrate", "Flow Rate Sensor", "","SEPA.SEP.Demonstrator.FlowRate");

	String tagNumber;
	String eventName;
	String description;
	String topic;
	
	DemonstratorVariables(String tagNumber, String eventName, String description, String topic)
	{
		this.tagNumber = tagNumber;
		this.eventName = eventName;
		this.description = description;
		this.topic = topic;
	}
	
	public String tagNumber()
	{
		return tagNumber;
	}
	
	public String eventName()
	{
		return eventName;
	}
	
	public String description()
	{
		return description;
	}
	
	public String topic()
	{
		String topic = "de.fzi.proasense.demonstrator." +tagNumber;
		return topic;
	}
}
