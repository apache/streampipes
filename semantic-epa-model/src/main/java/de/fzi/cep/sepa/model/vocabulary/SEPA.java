package de.fzi.cep.sepa.model.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDFS;


public enum SEPA {
	
	SEMANTICEVENTCONSUMER("SemanticEventConsumer", RDFS.CLASS),
	SEMANTICEVENTPRODUCER("SemanticEventProducer", RDFS.CLASS),
	SEMANTICEVENTPROCESSINGAGENT("SemanticEventProcessingAgent", RDFS.CLASS),
	EVENT_GROUNDING("EventGrounding", RDFS.CLASS),
	EVENT_SCHEMA("EventSchema", RDFS.CLASS),
	EVENT_SOURCE("EventSource", RDFS.CLASS),
	EVENT_QUALITY("EventQuality", RDFS.CLASS),
	EVENT_STREAM("EventStream", RDFS.CLASS),
	EVENT_PROPERTY("EventProperty", RDFS.CLASS),
	HAS_PROPERTY_TYPE("hasPropertyType",OWL.OBJECTPROPERTY),
	HAS_PROPERTY_NAME("hasPropertyName", OWL.DATATYPEPROPERTY),
	STATIC_PROPERTY("StaticProperty", RDFS.CLASS),
	OUTPUT_STRATEGY("OutputStrategy", RDFS.CLASS),
	PRODUCES("produces", OWL.OBJECTPROPERTY),
	HAS_GROUNDING("hasGrounding", OWL.OBJECTPROPERTY),
	REQUIRES("requires", OWL.OBJECTPROPERTY),
	HAS_OUTPUT_STRATEGY("hasOutputStrategy", OWL.OBJECTPROPERTY),
	HAS_EVENT_PROPERTY("hasEventProperty", OWL.OBJECTPROPERTY),
	HAS_UNIT("hasMeasurementUnit", OWL.OBJECTPROPERTY),
	HAS_NAME("hasName", OWL.DATATYPEPROPERTY),
	HAS_DESCRIPTION("hasDescription", OWL.DATATYPEPROPERTY),
	HAS_SCHEMA("hasSchema", OWL.OBJECTPROPERTY), 
	SEPAINVOCATIONGRAPH("SEPAInvocationGraph", RDFS.CLASS),
	SECINVOCATIONGRAPH("SECInvocationGraph", RDFS.CLASS);

	public static final String NAMESPACE = "http://sepa.event-processing.org/sepa#";
	
	public static final String PREFIX = "sepa";

	private String name;
	private URI type;
	
	ValueFactory f = ValueFactoryImpl.getInstance();;
	
	SEPA(String name, URI type)
	{
		this.name = name;
		this.type = type;
	}
	
	public String toURI()
	{
		return NAMESPACE + name;
	}
	
	public URI toSesameURI()
	{
		return f.createURI(NAMESPACE, name);
	}

	public URI getType() {
		return type;
	}
	
}
