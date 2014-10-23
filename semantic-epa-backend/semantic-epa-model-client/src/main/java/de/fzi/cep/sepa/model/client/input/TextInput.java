package de.fzi.cep.sepa.model.client.input;

import javax.persistence.Entity;

@Entity
public class TextInput extends FormInput {

	private String value;
	private String humanDescription;
	
	public TextInput(String humanDescription, String value) {
		super(ElementType.TEXT_INPUT);
		this.value = value;
		this.humanDescription = humanDescription;
		
	}
	
	public TextInput() {
		super(ElementType.TEXT_INPUT);
		
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getHumanDescription() {
		return humanDescription;
	}

	public void setHumanDescription(String humanDescription) {
		this.humanDescription = humanDescription;
	}
	
	

}
