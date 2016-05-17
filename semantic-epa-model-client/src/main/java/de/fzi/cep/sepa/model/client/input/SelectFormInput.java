package de.fzi.cep.sepa.model.client.input;

import java.util.List;

import javax.persistence.Entity;

@Entity
public class SelectFormInput extends SelectInput {

	public SelectFormInput()
	{
		super(ElementType.SELECT_INPUT);
	}
	
	public SelectFormInput(List<Option> options)
	{
		super(ElementType.SELECT_INPUT, options);
	}
}
