package de.fzi.cep.sepa.rest.api;

import de.fzi.cep.sepa.model.client.ontology.*;

import javax.ws.rs.core.Response;

public interface IOntologyKnowledge {

	Response getPropertyHierarchy();

	Response getProperty(String propertyId);

	Response addProperty(Resource elementData);

	Response updateProperty(String propertyId, Property propertyData);

	Response deleteProperty(String propertyId);


	Response getTypeHiearchy();

	Response getType(String typeId);

	Response addType(Resource elementData);

	Response updateType(String typeId, Concept elementData);

	Response deleteType(String typeId);


	Response addInstance(Resource elementData);

	Response getInstance(String instanceId);

	Response updateInstance(String instanceId, Instance elementData);

	Response deleteInstance(String instanceId);
	
	
	Response getNamespaces();

	Response addNamespace(Namespace namespace);
	
	Response deleteNamespace(String prefix);

	
	
}
