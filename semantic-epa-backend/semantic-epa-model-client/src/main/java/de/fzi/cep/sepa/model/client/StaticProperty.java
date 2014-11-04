package de.fzi.cep.sepa.model.client;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import de.fzi.cep.sepa.model.client.input.FormInput;

@Entity
public class StaticProperty extends SEPAElement {

	@OneToOne(cascade=CascadeType.ALL)
	FormInput input;
	
	
	StaticPropertyType type;
	
	public StaticProperty(StaticPropertyType type, String name, String description, FormInput input) {
		super(name, description);
		this.input = input;
		this.type = type;
	}

	public StaticProperty() {
		// TODO Auto-generated constructor stub
	}

	public FormInput getInput() {
		return input;
	}

	public void setInput(FormInput input) {
		this.input = input;
	}

	public StaticPropertyType getType() {
		return type;
	}

	public void setType(StaticPropertyType type) {
		this.type = type;
	}
	
	
	
}
