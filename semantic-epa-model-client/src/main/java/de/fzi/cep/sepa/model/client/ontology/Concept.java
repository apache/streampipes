package de.fzi.cep.sepa.model.client.ontology;

import java.util.ArrayList;
import java.util.List;

public class Concept extends OntologyElement {
	
	private List<Property> domainProperties;
	
	public Concept(ElementHeader elementHeader, String rdfsLabel, String rdfsDescription, List<Property> domainProperties)
	{
		super(elementHeader, rdfsLabel, rdfsDescription);
		this.domainProperties = domainProperties;
	}
	
	public Concept()
	{
		this.domainProperties = new ArrayList<>();
	}

	public Concept(ElementHeader header) {
		super(header);
	}

	public List<Property> getDomainProperties() {
		return domainProperties;
	}

	public void setDomainProperties(List<Property> domainProperties) {
		this.domainProperties = domainProperties;
	}
	

}
