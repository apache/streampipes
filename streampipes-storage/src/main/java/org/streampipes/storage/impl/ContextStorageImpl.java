package org.streampipes.storage.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

import org.streampipes.model.client.ontology.Context;
import org.streampipes.model.client.ontology.RdfFormat;
import org.streampipes.storage.api.ContextStorage;

public class ContextStorageImpl implements ContextStorage {

	private Repository repository;
	
	public ContextStorageImpl(Repository repository)
	{
		this.repository = repository;
	}
	
	
	public boolean addContext(Context context)
	{
		try {
			RepositoryConnection conn = getConnection();
			{
				ValueFactory vf = conn.getValueFactory();
				RDFParser rdfParser = getParser(context.getRdfFormat());
				StatementCollector handler = new StatementCollector();
				rdfParser.setRDFHandler(handler);
				rdfParser.parse(context.getInputStream(),  context.getBaseUri());
				Collection<Statement> col = handler.getStatements();
				Iterator<Statement> it = col.iterator();

				while(it.hasNext()) {
					org.openrdf.model.Statement statement = it.next();
					conn.add(statement, vf.createURI(context.getContextId()));
				}
			}
			closeConnection(conn);
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private RDFParser getParser(RdfFormat rdfFormat) {
		if (rdfFormat == RdfFormat.RDFXML) return Rio.createParser(RDFFormat.RDFXML);
		else if (rdfFormat == RdfFormat.JSONLD) return Rio.createParser(RDFFormat.JSONLD);
		else if (rdfFormat == RdfFormat.TURTLE) return Rio.createParser(RDFFormat.TURTLE);
		else if (rdfFormat == RdfFormat.RDFA) return Rio.createParser(RDFFormat.RDFA);
		else return Rio.createParser(RDFFormat.N3);
	}


	public boolean deleteContext(String contextId)
	{
		try {
			RepositoryConnection conn = getConnection();
			{
				conn.clear(conn.getValueFactory().createURI(contextId));
			}
			closeConnection(conn);
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public List<String> getAvailableContexts() {
		List<String> contexts = new ArrayList<>();
		
		try {
			RepositoryConnection conn = getConnection();
			{
				RepositoryResult<Resource> result = conn.getContextIDs();
				while(result.hasNext())
				{
					Resource resource = result.next();
					contexts.add(resource.stringValue());
				}
			}
			closeConnection(conn);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return contexts;
	}
	
	private RepositoryConnection getConnection() throws RepositoryException
	{
		return repository.getConnection();
	}
	
	private void closeConnection(RepositoryConnection connection) throws RepositoryException
	{
		connection.close();
	}
}
