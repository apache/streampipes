package de.fzi.cep.sepa.model.client.ontology;

public class OntologyQueryResponse {

	private String label;
	private String description;
	
	private String propertyValue;

	
	public OntologyQueryResponse(String label, String description,
			String propertyValue) {
		super();
		this.label = label;
		this.description = description;
		this.propertyValue = propertyValue;
	}

	public OntologyQueryResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
	
	
	
}
