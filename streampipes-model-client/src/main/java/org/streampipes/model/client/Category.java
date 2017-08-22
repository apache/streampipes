package org.streampipes.model.client;

public class Category {

	private String type;
	private String label;
	private String description;
	
	
	public Category(String type, String label, String description) {
		super();
		this.type = type;
		this.label = label;
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
