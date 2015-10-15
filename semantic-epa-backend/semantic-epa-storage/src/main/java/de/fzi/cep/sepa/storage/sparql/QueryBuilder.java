package de.fzi.cep.sepa.storage.sparql;

import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.model.client.ontology.PrimitiveRange;
import de.fzi.cep.sepa.model.client.ontology.Property;
import de.fzi.cep.sepa.model.client.ontology.RangeType;
import de.fzi.cep.sepa.model.vocabulary.SO;

public class QueryBuilder {

	public static final String RDFS_SUBCLASS_OF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	public static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String RDFS_CLASS = "http://www.w3.org/2000/01/rdf-schema#Class";
	public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
	public static final String RDFS_DESCRIPTION = "http://www.w3.org/2000/01/rdf-schema#description";
	public static final String RDFS_RANGE = "http://www.w3.org/2000/01/rdf-schema#range";
	public static final String RDFS_DOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
	public static final String SEPA = "http://sepa.event-processing.org/sepa#";
	
	public static String buildListSEPQuery() {
		StringBuilder builder = new StringBuilder();
		builder.append("where { ?result rdf:type sepa:SemanticEventProducer }");

		return builder.toString();
	}

	private static String getPrefix() {
		StringBuilder builder = new StringBuilder();
		builder.append("PREFIX sepa:<http://sepa.event-processing.org/sepa#>\n")
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
		builder.append(getPrefix() + " select ?label ?description ?domainPropertyId where { "
				+" <" +typeId +"> <" +RDFS_LABEL +"> " + " ?label ."
				+" <" +typeId +"> <" +RDFS_DESCRIPTION +"> " + " ?description ."
				+" ?domainPropertyId <" +RDFS_DOMAIN +"> <" +typeId +"> . }" );
		return builder.toString();
	}
	
	public static String getInstanceDetails(String instanceId) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?label ?description ?typeOf where { "
				+" <" +instanceId +"> <" +RDFS_LABEL +"> " + " ?label ."
				+" <" +instanceId +"> <" +RDFS_DESCRIPTION +"> " + " ?description . }");

		return builder.toString();
	}
	
	public static String getRdfType(String instanceId) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?typeOf where { "
				+" <" +instanceId +"> <" +RDF_TYPE +"> " + " ?typeOf . }");
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
	
	public static String getAutocompleteSuggestion(String propertyName) {
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?label ?value where {?s <" +RDF_TYPE +"> sepa:DomainConcept ."
				+ "?s <" +propertyName +"> ?value ."
				+ "?s rdfs:label ?label ."
				+ "}"
				);
		return builder.toString();
	}
	
	public static String getProperty(String propertyId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?label ?description ?range ?rangeType where {"
				+"<" +propertyId +"> <" +RDFS_LABEL +"> " + " ?label ."
				+"<" +propertyId +"> <" +RDFS_DESCRIPTION +"> " + " ?description ."
				+"<" +propertyId +"> <" +RDFS_RANGE +"> " + " ?range ."
				+"?range <" +RDF_TYPE +"> " + " ?rangeType ."
				+ "}");
		System.out.println(builder.toString());
		return builder.toString();
	}
	
	public static String getQuantitativeValueRange(String rangeId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() + " select ?minValue ?maxValue ?unitCode where {"
				+"<" +rangeId +"> <" +RDF_TYPE +"> " + " <http://schema.org/QuantitativeValue> ."
				+"<" +rangeId +"> <" +SO.MinValue +"> " + " ?minValue ."
				+"<" +rangeId +"> <" +SO.MaxValue +"> " + " ?maxValue ."
				+"<" +rangeId +"> <" +SO.UnitCode +"> " + " ?unitCode ."
				+ "}");
		
		return builder.toString();
	}
	
	public static String deletePropertyDetails(String propertyId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() +" DELETE { ?s ?p ?o }"
				+" WHERE { ?s ?p ?o ."
				+" FILTER (?s  = <" +propertyId +"> && ("
				+ " ?p = <" +RDFS_LABEL +"> || "
				+ " ?p = <" +RDFS_DESCRIPTION +"> || "
				+ " ?p = <" +RDFS_RANGE +"> )) } ");
				
		return builder.toString();
	}
	
	public static String deleteConceptDetails(String conceptId)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() +" DELETE { ?s ?p ?o }"
				+" WHERE { ?s ?p ?o ."
				+" FILTER ( (?s  = <" +conceptId +"> && ("
				+ " ?p = <" +RDFS_LABEL +"> || "
				+ " ?p = <" +RDFS_DESCRIPTION +">) || "
				+ " ( ?o = <" +conceptId +"> && ?p = <" +RDFS_DOMAIN +">) )) } ");
				
		return builder.toString();
	}
	
	public static String addConceptDetails(Concept concept)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() +" INSERT DATA {"
				+ "<" +concept.getElementHeader().getId() +"> <" +RDFS_LABEL +"> '" +concept.getRdfsLabel() +"' ."
				+ "<" +concept.getElementHeader().getId() +"> <" +RDFS_DESCRIPTION +"> '" +concept.getRdfsDescription() +"' .");
				
		concept.getDomainProperties().forEach(dp -> builder.append("<" +dp.getElementHeader().getId() +"> <" +RDFS_DOMAIN +"> <" +concept.getElementHeader().getId() +"> ."));
			
		builder.append(" }");
		return builder.toString();
		
	}
	
	public static String addPropertyDetails(Property property)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getPrefix() +" INSERT DATA {"
				+ "<" +property.getElementHeader().getId() +"> <" +RDFS_LABEL +"> '" +property.getRdfsLabel() +"' ."
				+ "<" +property.getElementHeader().getId() +"> <" +RDFS_DESCRIPTION +"> '" +property.getRdfsDescription() +"' .");
				
		if (property.getRange().getRangeType() == RangeType.PRIMITIVE)	
			builder.append("<" +property.getElementHeader().getId() +"> <" +RDFS_RANGE +"> <" +((PrimitiveRange)property.getRange()).getRdfsDatatype() +"> .");
		
		else if (property.getRange().getRangeType() == RangeType.QUANTITATIVE_VALUE)
			builder.append("<" +property.getElementHeader().getId() +"> <" +RDFS_RANGE +"> <" +SO.QuantitativeValue +"> .");
		
		else if (property.getRange().getRangeType() == RangeType.ENUMERATION)
			builder.append("");
			
		builder.append(" }");
		return builder.toString();
		
	}

}
