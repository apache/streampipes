package de.fzi.cep.sepa.storage.api;

import java.util.List;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.model.client.ontology.OntologyNode;
import de.fzi.cep.sepa.model.client.ontology.Property;


public interface BackgroundKnowledgeStorage {

	public List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	public List<OntologyNode> getPropertyHierarchy() throws RepositoryException, MalformedQueryException, QueryEvaluationException;
	
	public Property getProperty(String typeId);
	
	public Concept getConcept(String conceptId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	public boolean createProperty(Property property);
	
	public boolean createConcept(Concept concept);
	
	public boolean updateProperty(Property property);
}
