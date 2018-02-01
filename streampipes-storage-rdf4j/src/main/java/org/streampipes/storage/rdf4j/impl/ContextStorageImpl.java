package org.streampipes.storage.rdf4j.impl;

import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.streampipes.model.client.ontology.Context;
import org.streampipes.model.client.ontology.RdfFormat;
import org.streampipes.storage.api.IOntologyContextStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ContextStorageImpl implements IOntologyContextStorage {

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
					org.eclipse.rdf4j.model.Statement statement = it.next();
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
