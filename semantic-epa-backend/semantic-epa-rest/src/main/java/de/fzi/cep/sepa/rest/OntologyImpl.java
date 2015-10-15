package de.fzi.cep.sepa.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.messages.Notifications;
import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.model.client.ontology.Instance;
import de.fzi.cep.sepa.model.client.ontology.Namespace;
import de.fzi.cep.sepa.model.client.ontology.Property;
import de.fzi.cep.sepa.model.client.ontology.Resource;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Ontology;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

@Path("/ontology")
public class OntologyImpl extends AbstractRestInterface implements Ontology {

	@Path("/types")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getTypeHiearchy() {
		try {
			return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getClassHierarchy());
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			return null;
		}
		
	}

	@Path("/properties")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getPropertyHierarchy() {
		try {
			return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getPropertyHierarchy());
		} catch (RepositoryException | MalformedQueryException
				| QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Path("/properties/{propertyId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getProperty(@PathParam("propertyId") String propertyId) {
		try {
			return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getProperty(propertyId));
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			return toJson(Notifications.error("Could not load details"));
		}
	}

	@Path("/types/{typeId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getType(@PathParam("typeId") String typeId) {
		try {
			return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getConcept(typeId));
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return typeId;
	}

	@Override
	@Path("/namespaces")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getNamespaces() {
		try {
			return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getNamespaces());
		} catch (RepositoryException e) {
			e.printStackTrace();
			return toJson(Notifications.error("Could not connect to Sesame storage"));
		}
	}
	
	@Override
	@Path("/namespaces")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addNamespace(String namespace) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().addNamespace(fromJson(namespace, Namespace.class));
		if (success) return toJson(Notifications.success("Namespace successfully added."));
		else return toJson(Notifications.error("Could not add namespace. "));
	}

	@Override
	@Path("/namespaces/{prefix}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteNamespace(@PathParam("prefix") String prefix) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().deleteNamespace(prefix);
		if (success) return toJson(Notifications.success("Namespace successfully removed."));
		else return toJson(Notifications.error("Could not remove namespace. "));
	}

	@Override
	@Path("/properties")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addProperty(String elementData) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().addProperty(fromJson(elementData, Resource.class));
		if (success) return toJson(Notifications.success("Property successfully added."));
		else return toJson(Notifications.error("Could not add property. "));
	}

	@Override
	@Path("/types")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addType(String elementData) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().addConcept(fromJson(elementData, Resource.class));
		if (success) return toJson(Notifications.success("Concept successfully added."));
		else return toJson(Notifications.error("Could not add concept. "));
	}

	@Override
	@Path("/instances")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public String addInstance(String elementData) {
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().addIndividual(fromJson(elementData, Resource.class));
		if (success) return toJson(Notifications.success("Instance successfully added."));
		else return toJson(Notifications.error("Could not add instance. "));
	}

	@Override
	@Path("/properties/{propertyId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updateProperty(@PathParam("propertyId") String propertyId, String propertyData) {
		Property property = Utils.getGson().fromJson(propertyData, Property.class);
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().updateProperty(property);
		if (success) return toJson(Notifications.success("Property successfully updated."));
		else return toJson(Notifications.error("Could not update property. "));
		
	}

	@Override
	@Path("/types/{typeId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updateType(@PathParam("typeId") String typeId, String typeData) {
		Concept concept = Utils.getGson().fromJson(typeData, Concept.class);
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().updateConcept(concept);
		if (success) return toJson(Notifications.success("Concept successfully updated."));
		else return toJson(Notifications.error("Could not update concept. "));
	}
	
	@Path("/instances/{instanceId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getInstance(@PathParam("instanceId") String instanceId) {
		try {
			return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getInstance(instanceId));
		} catch (QueryEvaluationException | RepositoryException
				| MalformedQueryException e) {
			return toJson(Notifications.error("Instance not found"));
		}
	}

	@Override
	@Path("/instances/{instanceId}")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updateInstance(@PathParam("instanceId") String instanceId, String elementData) {
		Instance instance = Utils.getGson().fromJson(elementData, Instance.class);
		boolean success = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().updateInstance(instance);
		if (success) return toJson(Notifications.success("Instance successfully updated."));
		else return toJson(Notifications.error("Could not update instance. "));
	}

}
