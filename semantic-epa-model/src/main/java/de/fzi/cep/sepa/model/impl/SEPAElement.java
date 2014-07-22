package de.fzi.cep.sepa.model.impl;

import java.util.UUID;

public abstract class SEPAElement {

	protected String name;
	protected String description;
	protected String iconUrl;
	
	protected String elementId;
	
	public SEPAElement(String name, String description)
	{
		this.elementId = UUID.randomUUID().toString();
		this.name = name;
		this.description = description;
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

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	
	
	
}
