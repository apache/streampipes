package org.streampipes.storage.rdf4j.ontology;


import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

public class QueryExecutor {

	private Repository repository;
	
	public QueryExecutor(Repository repository)
	{
		this.repository = repository;
	}
	
	public TupleQueryResult executeQuery(String sparqlQuery) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		RepositoryConnection connection = repository.getConnection();
		TupleQuery tupleQuery;
		TupleQueryResult result;
			tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, sparqlQuery);
			result = tupleQuery.evaluate();
		
	    connection.close();
		return result;
	}
	
	public void executeUpdate(String sparqlUpdate) throws UpdateExecutionException, RepositoryException, MalformedQueryException
	{
		RepositoryConnection connection = repository.getConnection();
		Update tupleQuery;
		tupleQuery = connection.prepareUpdate(QueryLanguage.SPARQL, sparqlUpdate);
		tupleQuery.execute();
		
	    connection.close();
	}
	
}
