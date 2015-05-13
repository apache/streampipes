package de.fzi.cep.sepa.model.client.ontology;

public abstract class OntologyElement {

	private String rdfsLabel;
	
	private String rdfsDescription;
	
	public OntologyElement(String rdfsLabel, String rdfsDescription)
	{
		this.rdfsLabel = rdfsLabel;
		this.rdfsDescription = rdfsDescription;
	}
	
	public OntologyElement()
	{
		
	}

	public String getRdfsLabel() {
		return rdfsLabel;
	}

	public void setRdfsLabel(String rdfsLabel) {
		this.rdfsLabel = rdfsLabel;
	}

	public String getRdfsDescription() {
		return rdfsDescription;
	}

	public void setRdfsDescription(String rdfsDescription) {
		this.rdfsDescription = rdfsDescription;
	}
	
	
}
