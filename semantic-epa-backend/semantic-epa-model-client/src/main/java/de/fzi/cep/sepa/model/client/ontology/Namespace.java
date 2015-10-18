package de.fzi.cep.sepa.model.client.ontology;

public class Namespace {

	private String prefix;
	private String namespaceId;
	
	
	public Namespace(String prefix, String namespaceId) {
		super();
		this.prefix = prefix;
		this.namespaceId = namespaceId;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getNamespaceId() {
		return namespaceId;
	}
	public void setNamespaceId(String name) {
		this.namespaceId = name;
	}
	
	
}
