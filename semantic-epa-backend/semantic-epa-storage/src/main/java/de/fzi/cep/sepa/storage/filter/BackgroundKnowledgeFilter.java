package de.fzi.cep.sepa.storage.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.fzi.cep.sepa.model.client.ontology.OntologyNode;

public class BackgroundKnowledgeFilter {

	public static List<String> omittedPropertyPrefixes = Arrays.asList("http://purl.oclc.org/NET/ssnx/ssn#", "http://sepa.event-processing.org/sepa#", "http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	
	public static List<OntologyNode> propertiesFilter(List<OntologyNode> nodes)
	{
		return nodes.stream().filter(n -> !(omittedPropertyPrefixes.stream().anyMatch(op -> n.getId().startsWith(op)))).collect(Collectors.toList());
	}
	
	
	public static List<OntologyNode> classFilter(List<OntologyNode> nodes)
	{
		return nodes.stream().filter(n -> !(omittedPropertyPrefixes.stream().anyMatch(op -> n.getId().startsWith(op)))).collect(Collectors.toList());
	}
}
