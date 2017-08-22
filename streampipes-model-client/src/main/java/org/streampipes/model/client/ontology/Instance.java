package org.streampipes.model.client.ontology;

import java.util.ArrayList;
import java.util.List;

public class Instance extends OntologyElement {

private List<Property> domainProperties;
	
	public Instance(ElementHeader elementHeader, String rdfsLabel, String rdfsDescription, List<Property> domainProperties)
	{
		super(elementHeader, rdfsLabel, rdfsDescription);
		this.domainProperties = domainProperties;
	}
	
	public Instance()
	{
		this.domainProperties = new ArrayList<>();
	}

	public Instance(ElementHeader header) {
		super(header);
	}

	public List<Property> getDomainProperties() {
		return domainProperties;
	}

	public void setDomainProperties(List<Property> domainProperties) {
		this.domainProperties = domainProperties;
	}
}
