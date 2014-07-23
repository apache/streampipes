package de.fzi.cep.sepa.model.client.input;

public class TextInput extends FormInput {

	private String value;
	
	public TextInput(String humanDescription, String value) {
		super(ElementType.TEXT_INPUT);
		this.value = value;
		
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

}
