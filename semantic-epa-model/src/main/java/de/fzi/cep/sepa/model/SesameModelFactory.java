package de.fzi.cep.sepa.model;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.RDF;

import de.fzi.cep.sepa.model.vocabulary.SEPA;
import de.fzi.cep.sepa.util.EventUtils;

public class SesameModelFactory {

	private static ValueFactory factory = ValueFactoryImpl.getInstance();

	/*
	public static Statement createEPA(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.SEMANTICEVENTPROCESSINGAGENT);
	}
	
	public static Statement createSEP(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.SEMANTICEVENTPRODUCER);
	}
	
	public static Statement createEventProperty(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.EVENTPROPERTY);
	}
	
	public static Statement createEventGrounding(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.EVENTGROUNDING);
	}
	
	public static Statement createEventQuality(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.EVENTQUALITY);
	}
	
	public static Statement createEventSource(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.EVENTSOURCE);
	}
	
	public static Statement createEvent(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.EVENT);
	}
	
	public static Statement createEventPropertyType(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.PROPERTYTYPE);
	}
	
	public static Statement createMeasurementUnit(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.EVENTGROUNDING);
	}
	
	public static Statement createEventStream(URI name)
	{
		return factory.createStatement(name, RDF.TYPE, SEPA.EVENTSTREAM);
	}
	
	public static Statement createName(URI uri, String name)
	{
		return factory.createStatement(uri, SEPA.HASNAME, factory.createLiteral(name));
	}
	
	public static Statement createDescription(URI uri, String description)
	{
		return factory.createStatement(uri, SEPA.HASDESCRIPTION, factory.createLiteral(description));
	}
	
	public static Statement createEventSchema(URI uri)
	{
		return factory.createStatement(uri, RDF.TYPE, SEPA.EVENTSCHEMA);
	}
	
	public static URI createRandomUri(String ns)
	{
		return EventUtils.createRandomUri(ns);
	}
	
	public static Statement createHasSchema(URI event, URI schema)
	{
		return factory.createStatement(event, SEPA.HASSCHEMA, schema);
	}
	*/
}
