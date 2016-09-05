package de.fzi.cep.sepa.storage.api;

import java.util.List;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.model.client.ontology.Instance;
import de.fzi.cep.sepa.model.client.ontology.Namespace;
import de.fzi.cep.sepa.model.client.ontology.OntologyQuery;
import de.fzi.cep.sepa.model.client.ontology.Resource;
import de.fzi.cep.sepa.model.client.ontology.OntologyNode;
import de.fzi.cep.sepa.model.client.ontology.Property;


public interface BackgroundKnowledgeStorage {

	List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	List<OntologyNode> getPropertyHierarchy() throws RepositoryException, MalformedQueryException, QueryEvaluationException;
	
	
	Property getProperty(String typeId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	Concept getConcept(String conceptId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	Instance getInstance(String instanceId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	
	boolean addProperty(Resource resource);
	
	boolean addConcept(Resource resource);
	
	boolean addIndividual(Resource resource);
	
	
	boolean updateProperty(Property property);
	
	boolean updateConcept(Concept concept);
	
	boolean updateInstance(Instance instance);
	
	List<Namespace> getNamespaces() throws RepositoryException;
	
	boolean addNamespace(Namespace namespace);
	
	boolean deleteNamespace(String prefix);
	
	boolean deleteResource(String resourceId);
	
	OntologyQuery getOntologyResult(OntologyQuery query);
	
	boolean initialize();
	
	
}
