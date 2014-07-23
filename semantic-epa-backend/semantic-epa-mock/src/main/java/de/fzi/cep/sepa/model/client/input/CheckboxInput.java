package de.fzi.cep.sepa.model.client.input;

import java.util.List;

public class CheckboxInput extends SelectInput {

	public CheckboxInput()
	{
		super();
	}
	public CheckboxInput(List<Option> options) {
		super(ElementType.CHECKBOX, options);
	}


}
