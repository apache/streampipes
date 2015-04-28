package de.fzi.cep.sepa.messages;

public class ElementRecommendation {

	private String elementId;
	private String name;
	private String description;
	
	public ElementRecommendation(String elementId, String name, String description)
	{
		this.elementId = elementId;
		this.name = name;
		this.description = description;
	}
}
