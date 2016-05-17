package de.fzi.cep.sepa.storage.ontology;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;

public class ConceptUpdateExecutor extends UpdateExecutor {

	private Concept concept;
	
	public ConceptUpdateExecutor(Repository repository, Concept concept) {
		super(repository);
		this.concept = concept;
	}

	@Override
	public void deleteExistingTriples() throws RepositoryException,
			QueryEvaluationException, MalformedQueryException,
			UpdateExecutionException {
		executeUpdate(QueryBuilder.deleteConceptDetails(concept.getElementHeader().getId()));
	}

	@Override
	public void addNewTriples() throws RepositoryException,
			QueryEvaluationException, MalformedQueryException,
			UpdateExecutionException {
		executeUpdate(QueryBuilder.addConceptDetails(concept));
	}

}
