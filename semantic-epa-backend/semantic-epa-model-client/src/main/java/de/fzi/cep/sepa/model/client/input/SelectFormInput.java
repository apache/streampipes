package de.fzi.cep.sepa.model.client.input;

import java.util.List;

import javax.persistence.Entity;

@Entity
public class SelectFormInput extends SelectInput {

	public SelectFormInput()
	{
		// gson needs this
	}
	
	public SelectFormInput(List<Option> options)
	{
		super(ElementType.SELECT_INPUT, options);
	}
}
