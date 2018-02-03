package org.streampipes.storage.rdf4j.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.streampipes.model.client.ontology.OntologyNode;
import org.streampipes.model.client.ontology.Property;
import org.streampipes.storage.rdf4j.util.BackgroundKnowledgeUtils;

public class BackgroundKnowledgeFilter {

	public static List<String> omittedPropertyPrefixes = Arrays.asList("http://schema.org/Thing", "http://purl.oclc.org/NET/ssnx/ssn#", "http://sepa.event-processing.org/sepa#", "http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	
	public static List<OntologyNode> propertiesFilter(List<OntologyNode> nodes, boolean filterDuplicates)
	{
		List<OntologyNode> filteredList = nodes.stream().filter(n -> !(omittedPropertyPrefixes.stream().anyMatch(op -> n.getId().startsWith(op)))).collect(Collectors.toList());
		if (filterDuplicates) return BackgroundKnowledgeUtils.filterDuplicates(filteredList);
		else return filteredList;
	}
	
	public static List<Property> rdfsFilter(List<Property> properties, boolean filterDuplicates)
	{
		List<Property> filteredList = properties.stream().filter(n -> !(omittedPropertyPrefixes.stream().anyMatch(op -> n.getElementHeader().getId().startsWith(op)))).collect(Collectors.toList());
		if (filterDuplicates) return BackgroundKnowledgeUtils.filterDuplicates(filteredList);
		else return filteredList;
	}
	
	
	public static List<OntologyNode> classFilter(List<OntologyNode> nodes, boolean filterDuplicates)
	{
		List<OntologyNode> filteredList = nodes.stream().filter(n -> !(omittedPropertyPrefixes.stream().anyMatch(op -> n.getId().startsWith(op)))).collect(Collectors.toList());
		if (filterDuplicates) return BackgroundKnowledgeUtils.filterDuplicates(filteredList);
		else return filteredList;
	}
}
