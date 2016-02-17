package de.fzi.cep.sepa.model.client;

import javax.persistence.Entity;

@Entity
public class StreamClient extends SEPAElement {

	private String sourceId;
	private String category;
	
	public StreamClient(String name, String description, String sourceId)
	{
		super(name, description);
		this.sourceId = sourceId;
		this.category = sourceId;
	}
	
	public StreamClient(String name, String description, String sourceId, String iconName)
	{
		super(name, description, iconName);
		this.sourceId = sourceId;
		this.category = sourceId;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
