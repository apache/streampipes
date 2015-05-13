package de.fzi.cep.sepa.storage.impl;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.ClassHierarchy;
import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.model.client.ontology.NodeType;
import de.fzi.cep.sepa.model.client.ontology.OntologyNode;
import de.fzi.cep.sepa.model.client.ontology.Property;
import de.fzi.cep.sepa.model.client.ontology.PropertyHierarchy;
import de.fzi.cep.sepa.model.client.ontology.Statement;
import de.fzi.cep.sepa.storage.api.BackgroundKnowledgeStorage;
import de.fzi.cep.sepa.storage.ontology.ClassHierarchyBuilder;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;
import de.fzi.cep.sepa.storage.sparql.QueryExecutor;

public class BackgroundKnowledgeStorageImpl implements
		BackgroundKnowledgeStorage {

	Repository repo;

	public BackgroundKnowledgeStorageImpl(Repository repo) {
		this.repo = repo;
	}

	@Override
	public List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		return new ClassHierarchyBuilder(repo).makeClassHierarchy();
	}

	@Override
	public List<OntologyNode> getPropertyHierarchy() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		List<OntologyNode> ontologyNodes = new ArrayList<>();
		String queryString = QueryBuilder.getEventProperties();
			
		TupleQueryResult result = new QueryExecutor(repo.getConnection(), queryString).execute();
	
		while (result.hasNext()) { // iterate over the result
			BindingSet bindingSet = result.next();
			Value valueOfX = bindingSet.getValue("result");
			ontologyNodes.add(new OntologyNode(valueOfX.toString(), valueOfX.toString(), NodeType.PROPERTY));
		}
		return ontologyNodes;
	}

	@Override
	public Property getProperty(String propertyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Concept getConcept(String conceptId) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		TupleQueryResult result = new QueryExecutor(repo.getConnection(), QueryBuilder.getTypeDetails(conceptId)).execute();
		Concept concept = new Concept();
		List<Statement> statements = new ArrayList<>();
		while (result.hasNext()) { // iterate over the result
			BindingSet bindingSet = result.next();
			Value predicate = bindingSet.getValue("p");
			Value object = bindingSet.getValue("o");
			statements.add(new Statement(predicate.stringValue(), object.stringValue()));
		}
		concept.setStatements(statements);
		return concept;
	}

	@Override
	public boolean createProperty(Property property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createConcept(Concept concept) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateProperty(Property property) {
		// TODO Auto-generated method stub
		return false;
	}
}
