package de.fzi.cep.sepa.model.client;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

public abstract class ConsumableSEPAElement extends SEPAElement {
	
	@OneToMany(cascade=CascadeType.ALL)
	protected List<StaticProperty> staticProperties;

	public ConsumableSEPAElement(String name, String description) {
		super(name, description);
	}

	public ConsumableSEPAElement(String name, String description,
			String iconName) {
		super(name, description, iconName);
	}

	public List<StaticProperty> getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(List<StaticProperty> staticProperties) {
		this.staticProperties = staticProperties;
	}
	
	

}
