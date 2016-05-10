package de.fzi.cep.sepa.model.vocabulary;

public class MessageFormat {

	private static final String SEPA_NAMESPACE = "http://sepa.event-processing.org/sepa#";
	
	public static String Json = SEPA_NAMESPACE + "json";
    public static String Xml = SEPA_NAMESPACE + "xml";
    public static String Thrift = SEPA_NAMESPACE + "thrift";
 
}
