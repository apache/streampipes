package de.fzi.cep.sepa.model.client.input;

import java.util.List;

public class DomainConceptInput extends FormInput {
	
	private String requiredClass;
	
	private List<SupportedProperty> supportedProperties;
	
	public DomainConceptInput()
	{
		super(ElementType.DOMAIN_CONCEPT);
	}
	
	public DomainConceptInput(String requiredClass,
			List<SupportedProperty> supportedProperties) {
		super();
		this.requiredClass = requiredClass;
		this.supportedProperties = supportedProperties;
	}

	public String getRequiredClass() {
		return requiredClass;
	}

	public void setRequiredClass(String requiredClass) {
		this.requiredClass = requiredClass;
	}

	public List<SupportedProperty> getSupportedProperties() {
		return supportedProperties;
	}

	public void setSupportedProperties(List<SupportedProperty> supportedProperties) {
		this.supportedProperties = supportedProperties;
	}
	
	
}
