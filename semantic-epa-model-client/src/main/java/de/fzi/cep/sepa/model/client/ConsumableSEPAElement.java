package de.fzi.cep.sepa.model.client;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

public abstract class ConsumableSEPAElement extends SEPAElement {
	
	@OneToMany(cascade=CascadeType.ALL)
	protected List<StaticProperty> staticProperties;
	
	protected List<String> category;

	public ConsumableSEPAElement(String name, String description, List<String> category) {
		super(name, description);
		this.category = category;
	}

	public ConsumableSEPAElement(String name, String description,
			String iconName, List<String> category) {
		super(name, description, iconName);
		this.category = category;
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}	

}
