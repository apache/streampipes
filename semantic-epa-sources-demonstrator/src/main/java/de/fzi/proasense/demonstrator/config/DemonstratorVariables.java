package de.fzi.proasense.demonstrator.config;

public enum DemonstratorVariables {

    SIENENS_FLOWRATESENSOR1("flowrate1", "Flow Rate Sensor", "","de.fzi.proasense.demonstrator.siemens.flowrate.sensor1"),
    SIENENS_FLOWRATESENSOR2("flowrate2", "Flow Rate Sensor", "","de.fzi.proasense.demonstrator.siemens.flowrate.sensor2"),
    SIEMENS_LEVELSENSOR("level", "Level Sensor", "","de.fzi.proasense.demonstrator.siemens.level.sensor1"),

    FESTO_CONTAINERB101("container101", "Container101", "","de.fzi.proasense.demonstrator.festo.container.b101"),
    FESTO_CONTAINERB102("container102", "Container102", "","de.fzi.proasense.demonstrator.festo.container.b102"),
    FESTO_FLOWRATE("flowrate", "Flow Rate Sensor", "","de.fzi.proasense.demonstrator.festo.flowrate"),
    FESTO_PRESSURE_TANK("pressure", "Pressure Tank Sensor", "","de.fzi.proasense.demonstrator.festo.pressuretank"),
    ;

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
		return topic;
	}
}
