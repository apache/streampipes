package de.fzi.cep.sepa.model.client;

import de.fzi.cep.sepa.model.client.input.FormInput;

public class StaticProperty extends SEPAElement {

	FormInput input;
	
	public StaticProperty(String name, String description, FormInput input) {
		super(name, description);
		this.input = input;
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
}
