package de.fzi.cep.sepa.model;

import info.aduna.iteration.Iterations;
import de.fzi.cep.sepa.model.impl.SEPAFactory;
import de.fzi.cep.sepa.model.vocabulary.SEPA;
import edu.kit.aifb.cumulus.store.Store;
import edu.kit.aifb.cumulus.store.CassandraRdfHectorQuads;
import edu.kit.aifb.cumulus.store.CassandraRdfHectorTriple;
import edu.kit.aifb.cumulus.store.sesame.CumulusRDFSail;

import org.ontoware.rdf2go.RDF2Go;
import org.openrdf.sail.Sail;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

public class Main {

	public static void main(String[] args) throws Exception {

		//Store crdf;
		Model model = new LinkedHashModel(); 

	/*	
		crdf = new CassandraRdfHectorTriple("127.0.0.1:9160", "KeyspaceCumulus");
	
		Sail sail = new CumulusRDFSail(crdf);
		sail.initialize();

		SailRepository repo = new SailRepository(sail);
		RepositoryConnection conn = repo.getConnection();
*/
		ValueFactory f = ValueFactoryImpl.getInstance();
		
		String myNS = "http://my.ns.org/";
		
		URI twitterSEP = f.createURI(myNS + "twitterSEP");
		/*
		model.add(SesameModelFactory.createSEP(twitterSEP));
		model.add(SesameModelFactory.createName(twitterSEP, "Twitter Event"));
		model.add(SesameModelFactory.createDescription(twitterSEP, "A stream of twitter events."));
		
		URI event = SesameModelFactory.createRandomUri(myNS);
		URI eventSchema = SesameModelFactory.createRandomUri(myNS);
	
		
		
		model.add(SesameModelFactory.createEvent(event));
		model.add(SesameModelFactory.createEventSchema(eventSchema));
		
		model.add(SesameModelFactory.createHasSchema(event, eventSchema));
		
		for(int i = 0; i < 5; i++)
		{
			
		}
		
		*/
		
		//model.add(ModelFactory.createE)
		
		
		
		
			/*
		URI activitySEPA = f.createURI("http://my.ns.org/activitySEPA");
		Statement statement = f.createStatement(activitySEPA, RDF.TYPE, FOAF.KNOWS);
		
		conn.add(ModelFactory.createEPA(activitySEPA), activitySEPA);
		//conn.add(statement, );
		//conn.add(stat, arg1);
	//	conn.add(ModelFactory.
		
		conn.commit();
		try {
		crdf.addData(statement);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		*/

		
		/*
		RepositoryResult<Statement> statements = conn.getStatements(null,
				null, null, true);
		Model model = Iterations.addAll(statements, new LinkedHashModel());
		*/
		/*
		model.setNamespace("sepa", SEPA.NAMESPACE);
		model.setNamespace("rdf", RDF.NAMESPACE);
		model.setNamespace("rdfs", RDFS.NAMESPACE);
		model.setNamespace("xsd", XMLSchema.NAMESPACE);
		model.setNamespace("foaf", FOAF.NAMESPACE);
		//model.setNamespace("ex", namespace);

		Rio.write(model, System.out, RDFFormat.JSONLD);
		*/
		
		//CassandraClientMonitor monitor = crdf.getMonitor();
		//System.out.println(monitor.getNumActive());

		//crdf.addData(statement);
		//crdf.close();
	}
}
