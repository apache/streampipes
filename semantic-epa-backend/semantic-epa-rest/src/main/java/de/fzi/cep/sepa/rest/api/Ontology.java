package de.fzi.cep.sepa.rest.api;

public interface Ontology {

	public String getTypeHiearchy();
	
	public String getPropertyHierarchy();
	
	public String getProperty(String propertyId);
	
	public String getType(String typeId);	
	
}
