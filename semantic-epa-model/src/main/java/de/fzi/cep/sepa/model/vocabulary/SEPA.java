package de.fzi.cep.sepa.model.vocabulary;


public class SEPA {

	public static final String NAMESPACE = "http://sepa.event-processing.org/sepa#";
	
	public static final String PREFIX = "sepa";

	
	//public static final String NS = new NamespaceImpl(PREFIX, NAMESPACE);
	
	/** http://sepa.event-processing.org/sepa#SemanticEventProducer */
	public final static String SEMANTICEVENTPRODUCER;
	
	/** http://sepa.event-processing.org/sepa#SemanticEventProcessingAgent */
	public final static String SEMANTICEVENTPROCESSINGAGENT;
	
	public final static String EVENTGROUNDING;
	
	public final static String EVENTSOURCE;
	
	public final static String EVENTQUALITY;
	
	public final static String EVENT;
	
	public final static String EVENTSCHEMA;
	
	public final static String EVENTPROPERTY;
	
	public final static String PROPERTYTYPE;
	
	public final static String EVENTSTREAM;
	
	public final static String STATICPROPERTY;
	
	public final static String DATAPROPERTY;
	
	public final static String OUTPUTSTRATEGY;
	
	public final static String OPERATION;
	
	public final static String PRODUCES;
	
	public final static String HASGROUNDING;
	
	public final static String REQUIRES;
	
	public final static String HASOUTPUTSTRATEGY;
	
	public final static String PERFORMS;
	
	public final static String HASSOURCE;
	
	public final static String HASQUALITY;
	
	public final static String HASPROPERTY;
	
	public final static String HASPROPERTYTYPE;
	
	public final static String HASUNIT;
	
	public final static String HASNAME;
	
	public final static String HASDESCRIPTION;
	
	public final static String HASSCHEMA;
	
	static {
		
		HASUNIT = createString(SEPA.NAMESPACE, "hasUnit");
		HASPROPERTYTYPE = createString(SEPA.NAMESPACE, "hasPropertyType");
		HASPROPERTY = createString(SEPA.NAMESPACE, "hasProperty");
		HASQUALITY = createString(SEPA.NAMESPACE, "hasQuality");
		HASSOURCE = createString(SEPA.NAMESPACE, "hasSource");
		PERFORMS = createString(SEPA.NAMESPACE, "performs");
		HASOUTPUTSTRATEGY = createString(SEPA.NAMESPACE, "hasOutputStrategy");
		REQUIRES = createString(SEPA.NAMESPACE, "requires");
		HASGROUNDING = createString(SEPA.NAMESPACE, "hasGrounding");
		PRODUCES = createString(SEPA.NAMESPACE, "produces");
		HASNAME = createString(SEPA.NAMESPACE, "hasName");
		HASDESCRIPTION = createString(SEPA.NAMESPACE, "hasDescription");
		HASSCHEMA = createString(SEPA.NAMESPACE, "hasSchema");
		
		OPERATION = createString(SEPA.NAMESPACE, "Operation");
		OUTPUTSTRATEGY = createString(SEPA.NAMESPACE, "OutputStrategy");
		DATAPROPERTY = createString(SEPA.NAMESPACE, "DataProperty");
		STATICPROPERTY = createString(SEPA.NAMESPACE, "StaticProperty");
		EVENTSTREAM = createString(SEPA.NAMESPACE, "EventStream");
		PROPERTYTYPE = createString(SEPA.NAMESPACE, "PropertyType");
		EVENTPROPERTY = createString(SEPA.NAMESPACE, "EventProperty");
		EVENTSCHEMA = createString(SEPA.NAMESPACE, "EventSchema");
		EVENT = createString(SEPA.NAMESPACE, "Event");
		EVENTQUALITY = createString(SEPA.NAMESPACE, "EventQuality");
		EVENTSOURCE = createString(SEPA.NAMESPACE, "EventSource");
		EVENTGROUNDING = createString(SEPA.NAMESPACE, "EventGrounding");
		SEMANTICEVENTPROCESSINGAGENT = createString(SEPA.NAMESPACE, "SemanticEventProcessingAgent");
		SEMANTICEVENTPRODUCER = createString(SEPA.NAMESPACE, "SemanticEventProducer");
		
		
	}
	
	private static String createString(String uri, String name)
	{
		return uri + name;
	}
	
}
