package de.fzi.cep.sepa.webapp.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Stream;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;

public class Test {

	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, FileNotFoundException, IOException, RDFHandlerException
	{
		RepositoryConnection conn = StorageManager.INSTANCE.getConnection();
		
		Model schema = new LinkedHashModel();
		Model model = new LinkedHashModel();
		schema = Rio.parse(new InputStreamReader(Test.class.getResourceAsStream("/sepa-3.ttl")), "http://event-processing.org/sepa", RDFFormat.TURTLE);
		
		//model = Transformer.toRDF(new TwitterStreamProducer().declareModel());
		Stream<Statement> stream = model.stream();
		
		Iterator<Statement> it = stream.iterator();
		
		while(it.hasNext())
		{
			schema.add(it.next());
		}
		
		Rio.write(schema, System.out, RDFFormat.RDFXML);
		
		try {
			conn.add(model);
			System.out.println(QueryBuilder.getMatching());
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, QueryBuilder.getMatching());

			 TupleQueryResult result = tupleQuery.evaluate();
			 
			 try {
	              while (result.hasNext()) {  // iterate over the result
				BindingSet bindingSet = result.next();
				Value valueOfX = bindingSet.getValue("d");
				
				System.out.println(valueOfX.stringValue());

	              }
		  }
		  finally {
		      result.close();
		  }
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
