package de.fzi.cep.sepa.storage.ontology;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


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
		System.out.println(sparqlUpdate);
		RepositoryConnection connection = repository.getConnection();
		Update tupleQuery;
		tupleQuery = connection.prepareUpdate(QueryLanguage.SPARQL, sparqlUpdate);
		tupleQuery.execute();
		
	    connection.close();
	}
	
}
