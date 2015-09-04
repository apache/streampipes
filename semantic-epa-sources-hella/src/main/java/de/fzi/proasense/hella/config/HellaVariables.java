package de.fzi.proasense.hella.config;

public enum HellaVariables {

    MontracMovement("montrac", "Montrac Movement Data", "","SEPA.SEP.Montrac.MontracMovement"),
	IMM("moulding", "Moulding Parameters", "", "SEPA.SEP.Moulding.Parameters"),
	Scrap("visualInspection", "Scrap", "Scrap indicator of parts after visual inspection", "SEPA.SEP.VisualInspection.Scrap"),
	RawMaterialCertificate("materialCertificate", "Raw Material Certificate", "", "SEPA.SEP.Human.RawMaterialCertificate"),
	RawMaterialChange("materialChange", "Raw Material Change", "", "SEPA.SEP.Human.RawMaterialChange"),
	ProductionPlan("machinePlan", "Production Plan", "", "SEPA.SEP.Human.ProductionPlan");
	
	String tagNumber;
	String eventName;
	String description;
	String topic;
	
	HellaVariables(String tagNumber, String eventName, String description, String topic)
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
		String topic = "eu.proasense.internal.sp.internal.outgoing." +tagNumber;
		return topic;
	}
}
