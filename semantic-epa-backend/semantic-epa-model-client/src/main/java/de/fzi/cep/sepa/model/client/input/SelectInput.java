package de.fzi.cep.sepa.model.client.input;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public abstract class SelectInput extends FormInput {

	@OneToMany(cascade=CascadeType.ALL)
	List<Option> options;
	
	public SelectInput()
	{
		super();
	}
	public SelectInput(ElementType elementType, List<Option> options) {
		super(elementType);
		this.options = options;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	public boolean addOption(Option option)
	{
		return options.add(option);
	}

}
