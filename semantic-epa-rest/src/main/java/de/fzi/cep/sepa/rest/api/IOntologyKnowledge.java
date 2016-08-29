package de.fzi.cep.sepa.rest.api;

public interface IOntologyKnowledge {
	
	String getPropertyHierarchy();
	
	String getProperty(String propertyId);
	
	String addProperty(String elementData);
	
	String updateProperty(String propertyId, String propertyData);
	
	String deleteProperty(String propertyId);
	
	
	String getTypeHiearchy();
	
	String getType(String typeId);
	
	String addType(String elementData);
	
	String updateType(String typeId, String elementData);
	
	String deleteType(String typeId);
	
	
	String addInstance(String elementData);
	
	String getInstance(String instanceId);
	
	String updateInstance(String instanceId, String elementData);
	
	String deleteInstance(String instanceId);
	
	
	String getNamespaces();

	String addNamespace(String namespace);
	
	String deleteNamespace(String prefix);

	
	
}
