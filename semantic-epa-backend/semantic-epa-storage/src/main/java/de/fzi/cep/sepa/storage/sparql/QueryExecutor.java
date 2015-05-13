package de.fzi.cep.sepa.storage.sparql;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;


public class QueryExecutor {

	private String sparqlQuery;
	private RepositoryConnection connection;
	
	public QueryExecutor(RepositoryConnection connection, String sparqlQuery)
	{
		this.sparqlQuery = sparqlQuery;
		this.connection = connection;
	}
	
	public TupleQueryResult execute() throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		TupleQuery tupleQuery;
		TupleQueryResult result;
			tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, sparqlQuery);
			result = tupleQuery.evaluate();
		
		return result;
	}
}
