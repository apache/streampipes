package de.fzi.cep.sepa.model.client;

import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;

import de.fzi.cep.sepa.commons.Utils;

@Entity
public abstract class SEPAElement {

	protected String name;
	protected String description;
	protected String iconName;
	protected String iconUrl;

	protected String DOM;
	protected String elementId;
	List<String> connectedTo;
	
	protected boolean favorite;
	
	public SEPAElement()
	{
		// gson
	}
	
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

	public List<String> getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(List<String> connectedTo) {
		this.connectedTo = connectedTo;
	}

	public String getDOM() {
		return DOM;
	}

	public void setDOM(String dOM) {
		DOM = dOM;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
		
}
