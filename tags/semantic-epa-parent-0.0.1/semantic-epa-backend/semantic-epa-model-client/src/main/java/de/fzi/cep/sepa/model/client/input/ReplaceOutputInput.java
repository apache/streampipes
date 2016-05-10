package de.fzi.cep.sepa.model.client.input;

import java.util.ArrayList;
import java.util.List;

public class ReplaceOutputInput extends FormInput {

	private List<PropertyMapping> propertyMapping;
	
	public ReplaceOutputInput() {
		super(ElementType.REPLACE);
		this.propertyMapping = new ArrayList<>();
	}
	
	public ReplaceOutputInput(ElementType type)
	{
		super(type);
		this.propertyMapping = new ArrayList<>();
	}

	public List<PropertyMapping> getPropertyMapping() {
		return propertyMapping;
	}

	public void setPropertyMapping(List<PropertyMapping> propertyMapping) {
		this.propertyMapping = propertyMapping;
	}
	
	
}
