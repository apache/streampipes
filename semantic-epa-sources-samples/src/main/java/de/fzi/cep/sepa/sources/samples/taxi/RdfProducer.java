package de.fzi.cep.sepa.sources.samples.taxi;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

import de.fzi.cep.sepa.model.vocabulary.SEPA;

public class RdfProducer {

	private String medallion;
	
	private static final String DOMAIN_NS = "http://taxi.event-processing.org/taxi#";
	
	private final static String VEHICLE = DOMAIN_NS +"vehicle";
	
	private final static String HAS_PLATE_NUMBER = DOMAIN_NS +"hasPlateNumber";
	
	private final static String XSD_SUFFIX = "^^xsd:string";
	
	public RdfProducer(String medallion)
	{
		this.medallion = medallion;
	}
	
	public void toRdf()
	{
		ValueFactory vf = ValueFactoryImpl.getInstance();
		URI type = vf.createURI(RDF.TYPE.toString());
		Statement statement = vf.createStatement(vf.createURI(DOMAIN_NS+medallion), type, vf.createURI(VEHICLE));
		System.out.println(statement.toString());
	}
}
