package de.fzi.cep.sepa.model.client.ontology;

public abstract class OntologyElement {
	
	private ElementHeader elementHeader;
	
	private String rdfsLabel;
	private String rdfsDescription;
	
	public OntologyElement(ElementHeader elementHeader, String rdfsLabel, String rdfsDescription)
	{
		this.elementHeader = elementHeader;
		this.rdfsLabel = rdfsLabel;
		this.rdfsDescription = rdfsDescription;
	}
	
	public OntologyElement()
	{
		
	}

	public OntologyElement(ElementHeader header) {
		this.elementHeader = header;
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

	public ElementHeader getElementHeader() {
		return elementHeader;
	}

	public void setElementHeader(ElementHeader elementHeader) {
		this.elementHeader = elementHeader;
	}
		
}
