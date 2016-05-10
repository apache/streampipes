package de.fzi.cep.sepa.sources.samples.hella;

public enum HellaVariables {

    MontracMovement("montrac", "Montrac Movement Data (Replay)", "","SEPA.SEP.Montrac.MontracMovement"),
	IMM("moulding", "Moulding Parameters (Replay)", "", "SEPA.SEP.Moulding.Parameters"),
	Scrap("visualInspection", "Scrap (Replay)", "Scrap indicator of parts after visual inspection", "SEPA.SEP.VisualInspection.Scrap"),
	RawMaterialCertificate("materialCertificate", "Raw Material Certificate (Replay)", "", "SEPA.SEP.Human.RawMaterialCertificate"),
	RawMaterialChange("materialChange", "Raw Material Change (Replay)", "", "SEPA.SEP.Human.RawMaterialChange"),
	ProductionPlan("machinePlan", "Production Plan (Replay)", "", "SEPA.SEP.Human.ProductionPlan"), 
	EnrichedEvent("hella-enriched", "Hella Enriched Event (Replay)", "", "SEPA.SEP.Hella.Enriched"),
	Dust("hella-dust", "Dust Particle Sensor (Replay)", "", "SEPA.SEP.Hella.Dust");
	
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
