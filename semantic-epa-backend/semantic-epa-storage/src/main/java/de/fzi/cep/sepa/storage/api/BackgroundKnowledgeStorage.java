package de.fzi.cep.sepa.storage.api;

import java.util.List;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.model.client.ontology.Instance;
import de.fzi.cep.sepa.model.client.ontology.Namespace;
import de.fzi.cep.sepa.model.client.ontology.Resource;
import de.fzi.cep.sepa.model.client.ontology.OntologyNode;
import de.fzi.cep.sepa.model.client.ontology.Property;


public interface BackgroundKnowledgeStorage {

	public List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	public List<OntologyNode> getPropertyHierarchy() throws RepositoryException, MalformedQueryException, QueryEvaluationException;
	
	
	public Property getProperty(String typeId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	public Concept getConcept(String conceptId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	public Instance getInstance(String instanceId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	
	public boolean addProperty(Resource resource);
	
	public boolean addConcept(Resource resource);
	
	public boolean addIndividual(Resource resource);
	
	
	public boolean updateProperty(Property property);
	
	public boolean updateConcept(Concept concept);
	
	public boolean updateInstance(Instance instance);
	
	public List<Namespace> getNamespaces() throws RepositoryException;
	
	public boolean addNamespace(Namespace namespace);
	
	public boolean deleteNamespace(String prefix);
	
	
	
}
