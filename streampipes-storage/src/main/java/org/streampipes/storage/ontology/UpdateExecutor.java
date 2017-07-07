package org.streampipes.storage.ontology;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

public abstract class UpdateExecutor extends QueryExecutor {

	public UpdateExecutor(Repository repository) {
		super(repository);
	}
	
	public abstract void deleteExistingTriples() throws RepositoryException, QueryEvaluationException, MalformedQueryException, UpdateExecutionException;
	
	public abstract void addNewTriples() throws RepositoryException, QueryEvaluationException, MalformedQueryException, UpdateExecutionException;

}
