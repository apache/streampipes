package org.streampipes.model.client.ontology;

import java.util.List;

public class OntologyQuery {

	private String requiredClass;
	
	private List<OntologyQueryItem> requiredProperties;

	
	public OntologyQuery(String requiredClass,
			List<OntologyQueryItem> requiredProperties) {
		super();
		this.requiredClass = requiredClass;
		this.requiredProperties = requiredProperties;
	}

	public String getRequiredClass() {
		return requiredClass;
	}

	public void setRequiredClass(String requiredClass) {
		this.requiredClass = requiredClass;
	}

	public List<OntologyQueryItem> getRequiredProperties() {
		return requiredProperties;
	}

	public void setRequiredProperties(List<OntologyQueryItem> requiredProperties) {
		this.requiredProperties = requiredProperties;
	}
	
	
}
