package de.fzi.cep.sepa.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.Ontology;
import de.fzi.cep.sepa.storage.controller.StorageManager;

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
		return toJson(StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getProperty(propertyId));
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

}
