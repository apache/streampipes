package de.fzi.cep.sepa.model.client;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class StreamClient extends SEPAElement {

	private String sourceId;
	private List<String> category;
	
	public StreamClient(String name, String description, String sourceId)
	{
		super(name, description);
		this.sourceId = sourceId;
		this.category = Arrays.asList(sourceId);
	}
	
	public StreamClient(String name, String description, String sourceId, String iconName)
	{
		super(name, description, iconName);
		this.sourceId = sourceId;
		this.category = Arrays.asList(sourceId);
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}
	
	
}
