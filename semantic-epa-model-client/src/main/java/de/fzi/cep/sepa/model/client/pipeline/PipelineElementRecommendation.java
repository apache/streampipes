package de.fzi.cep.sepa.model.client.pipeline;

public class PipelineElementRecommendation {

	private String elementId;
	private String name;
	private String description;
	
	public PipelineElementRecommendation(String elementId, String name, String description)
	{
		this.elementId = elementId;
		this.name = name;
		this.description = description;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
