package de.fzi.cep.sepa.model.client.pipeline;

public class PipelineElementRecommendation {

	private String elementId;
	private String name;
	private String description;
	private Float weight;
	private Integer count;

	public PipelineElementRecommendation() {

	}

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

	public Float getWeight() {
		return weight;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
