package org.streampipes.storage.rdf4j.ontology;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.streampipes.model.client.ontology.Concept;
import org.streampipes.storage.rdf4j.sparql.QueryBuilder;

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
