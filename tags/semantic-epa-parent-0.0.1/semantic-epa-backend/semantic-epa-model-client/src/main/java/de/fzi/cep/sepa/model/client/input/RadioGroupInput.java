package de.fzi.cep.sepa.model.client.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class RadioGroupInput extends FormInput {

	private List<Option> optionLeft;
	private List<Option> optionRight;
		
	public RadioGroupInput()
	{
		super(ElementType.RADIO_GROUP_INPUT);
		this.optionLeft = new ArrayList<Option>();
		this.optionRight = new ArrayList<Option>();
	}
	
	public RadioGroupInput(List<Option> optionLeft, List<Option> optionRight)
	{
		super(ElementType.RADIO_GROUP_INPUT);
		this.optionLeft = optionLeft;
		this.optionRight = optionRight;
	}
	
	public RadioGroupInput(ElementType type, String name, String description)
	{
		super(type);
		this.optionLeft = new ArrayList<Option>();
		this.optionRight = new ArrayList<Option>();
	}

	public List<Option> getOptionLeft() {
		return optionLeft;
	}

	public void setOptionLeft(List<Option> optionLeft) {
		this.optionLeft = optionLeft;
	}

	public List<Option> getOptionRight() {
		return optionRight;
	}

	public void setOptionRight(List<Option> optionRight) {
		this.optionRight = optionRight;
	}
	
}
