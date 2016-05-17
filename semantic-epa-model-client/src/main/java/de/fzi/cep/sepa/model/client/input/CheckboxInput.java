package de.fzi.cep.sepa.model.client.input;

import java.util.List;

import javax.persistence.Entity;
@Entity
public class CheckboxInput extends SelectInput {

	public CheckboxInput()
	{
		super(ElementType.CHECKBOX);
	}
	public CheckboxInput(List<Option> options) {
		super(ElementType.CHECKBOX, options);
	}


}
