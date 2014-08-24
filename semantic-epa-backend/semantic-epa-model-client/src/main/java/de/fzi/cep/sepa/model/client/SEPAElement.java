package de.fzi.cep.sepa.model.client;

import java.util.UUID;

import de.fzi.cep.sepa.commons.Utils;

public abstract class SEPAElement {

	protected String name;
	protected String description;
	protected String iconName;
	protected String iconUrl;

	
	protected String elementId;
	
	public SEPAElement(String name, String description)
	{
		this.elementId = UUID.randomUUID().toString();
		this.name = name;
		this.description = description;
	}
	
	public SEPAElement(String name, String description, String iconName)
	{
		this(name, description);
		this.iconName = iconName;
		this.iconUrl = Utils.getImageUrl() +iconName +"_HQ.png";
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

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	
}
