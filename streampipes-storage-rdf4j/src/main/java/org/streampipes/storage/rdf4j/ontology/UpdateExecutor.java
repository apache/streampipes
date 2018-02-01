package org.streampipes.storage.rdf4j.ontology;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;

public abstract class UpdateExecutor extends QueryExecutor {

	public UpdateExecutor(Repository repository) {
		super(repository);
	}
	
	public abstract void deleteExistingTriples() throws RepositoryException, QueryEvaluationException, MalformedQueryException, UpdateExecutionException;
	
	public abstract void addNewTriples() throws RepositoryException, QueryEvaluationException, MalformedQueryException, UpdateExecutionException;

}
