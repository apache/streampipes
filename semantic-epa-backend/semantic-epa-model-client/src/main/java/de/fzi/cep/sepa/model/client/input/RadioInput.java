package de.fzi.cep.sepa.model.client.input;

import java.util.List;

public class RadioInput extends SelectInput {
	
	public RadioInput(List<Option> options) {
		super(ElementType.RADIO_INPUT, options);
		
	}


}
