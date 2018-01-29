package org.streampipes.storage.rdf4j.ontology;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.streampipes.model.client.ontology.Instance;
import org.streampipes.storage.rdf4j.sparql.QueryBuilder;

public class InstanceUpdateExecutor extends UpdateExecutor {

private Instance instance;
	
	public InstanceUpdateExecutor(Repository repository, Instance instance) {
		super(repository);
		this.instance = instance;
	}

	@Override
	public void deleteExistingTriples() throws RepositoryException,
					QueryEvaluationException, MalformedQueryException,
					UpdateExecutionException {
		executeUpdate(QueryBuilder.deleteInstanceDetails(instance.getElementHeader().getId()));
	}

	@Override
	public void addNewTriples() throws RepositoryException,
			QueryEvaluationException, MalformedQueryException,
			UpdateExecutionException {
		executeUpdate(QueryBuilder.addInstanceDetails(instance));
	}
}
