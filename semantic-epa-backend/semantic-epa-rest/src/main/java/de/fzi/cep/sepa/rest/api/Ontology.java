package de.fzi.cep.sepa.rest.api;

public interface Ontology {
	
	public String getPropertyHierarchy();
	
	public String getProperty(String propertyId);
	
	public String addProperty(String elementData);
	
	public String updateProperty(String propertyId, String propertyData);
	
	public String deleteProperty(String propertyId);
	
	
	public String getTypeHiearchy();
	
	public String getType(String typeId);	
	
	public String addType(String elementData);
	
	public String updateType(String typeId, String elementData);
	
	public String deleteType(String typeId);
	
	
	public String addInstance(String elementData);
	
	public String getInstance(String instanceId);
	
	public String updateInstance(String instanceId, String elementData);
	
	public String deleteInstance(String instanceId);
	
	
	public String getNamespaces();

	public String addNamespace(String namespace);
	
	public String deleteNamespace(String prefix);

	
	
}
