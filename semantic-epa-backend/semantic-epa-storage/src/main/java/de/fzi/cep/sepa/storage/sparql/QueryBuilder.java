package de.fzi.cep.sepa.storage.sparql;

public class QueryBuilder {

	public static final String RDFS_SUBCLASS_OF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	public static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String RDFS_CLASS = "http://www.w3.org/2000/01/rdf-schema#Class";
	
	public static String buildListSEPQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("where { ?result rdf:type sepa:SemanticEventProducer }");

		return builder.toString();
	}

	private static String getPrefix() {
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX sepa:<http://event-processing.org/sepa#>\n")
				.append("PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");

		return builder.toString();
	}

	public static String getMatching() {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix())
				.append("select ?d where {?a rdf:type sepa:EventProperty. ?b rdf:type sepa:EventSchema. ?b sepa:hasEventProperty ?a. ?a sepa:hasPropertyName ?d}");
		return builder.toString();
	}

	public static String getAllStatements() {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix()).append("select ?a where { ?a ?b ?c }");

		return builder.toString();
	}

	public static String buildSEPByDomainQuery(String domain) {
		StringBuilder builder = new StringBuilder();
		builder.append("where { ?result rdf:type sepa:SemanticEventProducer. ?result sepa:hasDomain '"
				+ domain + "'^^xsd:string }");

		return builder.toString();
	}

	public static String buildSEPAByDomainQuery(String domain) {
		StringBuilder builder = new StringBuilder();
		builder.append("where { ?result rdf:type sepa:SemanticEventProcessingAgent. ?result sepa:hasDomain '"
				+ domain + "'^^xsd:string }");

		return builder.toString();
	}

	public static String buildListSEPAQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("where { ?result rdf:type sepa:SemanticEventProcessingAgent }");

		return builder.toString();
	}

	public static String buildListSECQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("where { ?result rdf:type sepa:SemanticEventConsumer }");

		return builder.toString();
	}
	
	public static String getClasses() {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?result where {?result rdf:type <http://www.w3.org/2000/01/rdf-schema#Class>}");
		return builder.toString();
	}

	public static String getEventProperties() {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?result where {?result rdf:type <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property>}");
		return builder.toString();
	}
	
	public static String getTypeDetails(String typeId) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?p ?o where {<" +typeId +"> ?p ?o}");
		return builder.toString();
	}
	
	public static String getSubclasses(String className)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?s where {?s <" +RDFS_SUBCLASS_OF +"> <" +className +">}");
		return builder.toString();
	}

	public static String getInstances(String className) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?s where {?s <" +RDF_TYPE +"> <" +className +">}");
		return builder.toString();
	}

}
