package de.fzi.cep.sepa.storage.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import de.fzi.cep.sepa.model.client.ontology.Concept;
import de.fzi.cep.sepa.model.client.ontology.ElementHeader;
import de.fzi.cep.sepa.model.client.ontology.Instance;
import de.fzi.cep.sepa.model.client.ontology.Namespace;
import de.fzi.cep.sepa.model.client.ontology.NodeType;
import de.fzi.cep.sepa.model.client.ontology.OntologyNode;
import de.fzi.cep.sepa.model.client.ontology.OntologyQuery;
import de.fzi.cep.sepa.model.client.ontology.OntologyQueryItem;
import de.fzi.cep.sepa.model.client.ontology.OntologyQueryResponse;
import de.fzi.cep.sepa.model.client.ontology.Property;
import de.fzi.cep.sepa.model.client.ontology.Resource;
import de.fzi.cep.sepa.storage.api.BackgroundKnowledgeStorage;
import de.fzi.cep.sepa.storage.filter.BackgroundKnowledgeFilter;
import de.fzi.cep.sepa.storage.ontology.ClassHierarchyExecutor;
import de.fzi.cep.sepa.storage.ontology.ConceptUpdateExecutor;
import de.fzi.cep.sepa.storage.ontology.InstanceUpdateExecutor;
import de.fzi.cep.sepa.storage.ontology.PropertyUpdateExecutor;
import de.fzi.cep.sepa.storage.ontology.QueryExecutor;
import de.fzi.cep.sepa.storage.ontology.RangeQueryExecutor;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;
import de.fzi.cep.sepa.storage.util.BackgroundKnowledgeUtils;

public class BackgroundKnowledgeStorageImpl implements
		BackgroundKnowledgeStorage {

	Repository repo;

	public BackgroundKnowledgeStorageImpl(Repository repo) {
		this.repo = repo;
	}

	@Override
	public List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		return new ClassHierarchyExecutor(repo).getClassHierarchy();
	}

	@Override
	public List<OntologyNode> getPropertyHierarchy() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		List<OntologyNode> ontologyNodes = new ArrayList<>();
		String queryString = QueryBuilder.getEventProperties();
			
		TupleQueryResult result = getQueryResult(queryString);
	
		while (result.hasNext()) { // iterate over the result
			BindingSet bindingSet = result.next();
			Value valueOfX = bindingSet.getValue("result");
			Optional<Namespace> ns = getNamespace(valueOfX.toString());
			if (ns.isPresent()) ontologyNodes.add(new OntologyNode(valueOfX.toString(), valueOfX.toString().replace(ns.get().getNamespaceId(), ns.get().getPrefix() +":"), ns.get().getPrefix(), ns.get().getNamespaceId(), NodeType.PROPERTY));
			else ontologyNodes.add(new OntologyNode(valueOfX.toString(), valueOfX.toString(), NodeType.PROPERTY));
		}

		return BackgroundKnowledgeFilter.propertiesFilter(ontologyNodes, true);
	}
	
	private Property getProperty(String propertyId, String instanceId) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		ElementHeader header = null;
		Property property = null;
		RangeQueryExecutor rangeExecutor = null;
		
		String label = "";
		String description = "";
		String range = "";
		List<String> rangeTypes = new ArrayList<>();;
			
		String queryString = QueryBuilder.getProperty(propertyId);
		TupleQueryResult result = getQueryResult(queryString);
		
		Optional<Namespace> nsOpt = getNamespace(propertyId);
		if (nsOpt.isPresent()) 
			{
				Namespace ns = nsOpt.get();
				header = new ElementHeader(propertyId, propertyId.replace(ns.getNamespaceId(), ns.getPrefix() +":"), ns.getPrefix(), ns.getNamespaceId());
			}
		else
			header = new ElementHeader(propertyId, propertyId);
		
		while(result.hasNext())
		{
			BindingSet bindingSet = result.next();
			Value labelField = bindingSet.getValue("label");
			Value descriptionField = bindingSet.getValue("description");
			range = bindingSet.getValue("range").stringValue();
			rangeTypes.add(bindingSet.getValue("rangeType").stringValue());	
			
			if (labelField != null) label = labelField.stringValue();
			if (descriptionField != null) description = descriptionField.stringValue();
		}
		
		if (instanceId == null) rangeExecutor = new RangeQueryExecutor(repo, propertyId, range, rangeTypes);
		else rangeExecutor = new RangeQueryExecutor(repo, propertyId, range, rangeTypes, instanceId);
		property = new Property(header, label, description, rangeExecutor.getRange());
		property.setRangeDefined((range != null && !range.equals("")) ? true : false);
		
		return property;
	}

	@Override
	public Property getProperty(String propertyId) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		return getProperty(propertyId, null);
	}

	@Override
	public Concept getConcept(String conceptId) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		TupleQueryResult result =getQueryResult(QueryBuilder.getTypeDetails(conceptId));
		
		Concept concept = new Concept();
		ElementHeader header = null;
		
		List<Property> properties = new ArrayList<>();
		int idx = 0;
		
		Optional<Namespace> nsOpt = getNamespace(conceptId);
		if (nsOpt.isPresent()) 
			{
				Namespace ns = nsOpt.get();
				header = new ElementHeader(conceptId, conceptId.replace(ns.getNamespaceId(), ns.getPrefix() +":"), ns.getPrefix(), ns.getNamespaceId());
			}
		else
			header = new ElementHeader(conceptId, conceptId);
		
		concept.setElementHeader(header);
		
		while (result.hasNext()) { 
			BindingSet bindingSet = result.next();
			if (idx == 0)
			{
				Value label = bindingSet.getValue("label");
				Value description = bindingSet.getValue("description");
				if (label != null) concept.setRdfsLabel(label.stringValue());
				if (description != null) concept.setRdfsDescription(description.stringValue());
			}
			Value domainPropertyId = bindingSet.getValue("domainPropertyId");
			Property property = getProperty(domainPropertyId.stringValue());
			properties.add(property);
		
			idx++;
		}
		
		concept.setDomainProperties(BackgroundKnowledgeUtils.filterDuplicates(properties));
		return concept;
	}

	@Override
	public boolean updateProperty(Property property) {
		PropertyUpdateExecutor propertyUpdateExecutor = new PropertyUpdateExecutor(repo, property);
		
		try {
			propertyUpdateExecutor.deleteExistingTriples();
			propertyUpdateExecutor.addNewTriples();
			return true;
		} catch (RepositoryException | MalformedQueryException | UpdateExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Namespace> getNamespaces() throws RepositoryException {
		
		List<Namespace> result = new ArrayList<>();
		RepositoryResult<org.openrdf.model.Namespace> namespaces = repo.getConnection().getNamespaces();
		
		while(namespaces.hasNext())
		{
			org.openrdf.model.Namespace ns = namespaces.next();
			result.add(new Namespace(ns.getPrefix(), ns.getName()));
		}
		return result;
		
	}
	
	private Optional<Namespace> getNamespace(String propertyId)
	{
		return BackgroundKnowledgeUtils.getNamespace(propertyId);
	}
	
	private TupleQueryResult getQueryResult(String queryString) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		 return new QueryExecutor(repo).executeQuery(queryString);
	}

	@Override
	public boolean addNamespace(Namespace namespace) {
		
		try {
			RepositoryConnection conn = repo.getConnection();
			conn.setNamespace(namespace.getPrefix(), namespace.getNamespaceId());
			conn.close();
			return true;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		} 
	}

	@Override
	public boolean deleteNamespace(String prefix) {
		try {
			RepositoryConnection conn = repo.getConnection();
			conn.removeNamespace(prefix); 
			conn.close();
			return true;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		} 
	}

	@Override
	public boolean addProperty(Resource resource) {
		return addResource(resource, RDF.PROPERTY);
	}

	@Override
	public boolean addConcept(Resource resource) {
		return addResource(resource, RDFS.CLASS);
	}

	@Override
	public boolean addIndividual(Resource resource) {
		try {
			RepositoryConnection conn = repo.getConnection();
			ValueFactory factory = conn.getValueFactory();
			String elementName = resource.getElementName().replaceAll(" ", "_");
			org.openrdf.model.Statement st;
			
			if (resource.getInstanceOf() != null ) st = factory.createStatement(factory.createURI(resource.getNamespace() +elementName), RDF.TYPE, factory.createURI(resource.getInstanceOf()));
			else st = factory.createStatement(factory.createURI(resource.getNamespace() +elementName), RDF.TYPE, RDFS.RESOURCE);
			
			conn.add(st);
			conn.close();
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}
	
	private boolean addResource(Resource resource, org.openrdf.model.URI object)
	{
		try {
			RepositoryConnection conn = repo.getConnection();
			ValueFactory factory = conn.getValueFactory();
			String elementName = resource.getElementName().replaceAll(" ", "_");
			org.openrdf.model.Statement st = factory.createStatement(factory.createURI(resource.getNamespace()+elementName), RDF.TYPE, object);
			conn.add(st);
			conn.close();
			return true;
		} catch (RepositoryException e) {
			return false;
		}
	}

	@Override
	public boolean updateConcept(Concept concept) {
		ConceptUpdateExecutor conceptUpdateExecutor = new ConceptUpdateExecutor(repo, concept);
		try {
			conceptUpdateExecutor.deleteExistingTriples();
			conceptUpdateExecutor.addNewTriples();
			return true;
		} catch (RepositoryException | MalformedQueryException | UpdateExecutionException | QueryEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Instance getInstance(String instanceId)
			throws QueryEvaluationException, RepositoryException,
			MalformedQueryException {
		TupleQueryResult result =getQueryResult(QueryBuilder.getInstanceDetails(instanceId));
		Instance instance = new Instance();
		ElementHeader header = null;
		
		List<Property> properties = new ArrayList<>();
		List<String> instanceProperties = new ArrayList<>();
		int idx = 0;
		
		Optional<Namespace> nsOpt = getNamespace(instanceId);
		if (nsOpt.isPresent()) 
			{
				Namespace ns = nsOpt.get();
				header = new ElementHeader(instanceId, instanceId.replace(ns.getNamespaceId(), ns.getPrefix() +":"), ns.getPrefix(), ns.getNamespaceId());
			}
		else
			header = new ElementHeader(instanceId, instanceId);
		
		instance.setElementHeader(header);
		
		while (result.hasNext()) { 
			BindingSet bindingSet = result.next();
			
			if (idx == 0)
			{
				Value label = bindingSet.getValue("label");
				Value description = bindingSet.getValue("description");
				if (label != null) instance.setRdfsLabel(label.stringValue());
				if (description != null) instance.setRdfsDescription(description.stringValue());
			}
			
			instanceProperties.add(bindingSet.getValue("property").stringValue());
			
			idx++;
		}
		
		for(String propertyId : instanceProperties)
		{
			Property p = getProperty(propertyId, instanceId);
			properties.add(p);
		}
		
		List<String> rdfTypes = getRdfTypes(instanceId);
		rdfTypes
			.stream()
			.filter(type -> !BackgroundKnowledgeFilter.omittedPropertyPrefixes
					.stream()
					.anyMatch(prefix -> type.equals(prefix)))
			.forEach(type -> {
				try {
					Concept concept = getConcept(type);
					
					concept.getDomainProperties()
						.stream()
						.filter(dp -> !properties
								.stream()
								.anyMatch(p -> p.getElementHeader().getId().equals(dp.getElementHeader().getId())))
						.forEach(dp -> properties.add(dp));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});;
		
		
		instance.setDomainProperties(BackgroundKnowledgeUtils.filterDuplicates(BackgroundKnowledgeFilter.rdfsFilter(properties, true)));
		return instance;
	}

	private List<String> getRdfTypes(String instanceId) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		List<String> rdfTypes = new ArrayList<>();
		TupleQueryResult result =getQueryResult(QueryBuilder.getRdfType(instanceId));
		while (result.hasNext()) { 
			BindingSet bindingSet = result.next();
			Value typeOf = bindingSet.getValue("typeOf");
			rdfTypes.add(typeOf.stringValue());
		}
		
		return rdfTypes;
	}

	@Override
	public boolean updateInstance(Instance instance) {
		InstanceUpdateExecutor instanceUpdateExecutor = new InstanceUpdateExecutor(repo, instance);
		try {
			instanceUpdateExecutor.deleteExistingTriples();
			instanceUpdateExecutor.addNewTriples();
			return true;
		} catch (RepositoryException | MalformedQueryException | UpdateExecutionException | QueryEvaluationException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteResource(String resourceId) {
		
		String deleteQuery = QueryBuilder.deleteResource(resourceId);
		try {
			RepositoryConnection connection = repo.getConnection();
			Update tupleQuery;
			tupleQuery = connection.prepareUpdate(QueryLanguage.SPARQL, deleteQuery);
			tupleQuery.execute();
			
		    connection.close();
		    return true;
		} catch (RepositoryException | MalformedQueryException | UpdateExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public OntologyQuery getOntologyResult(OntologyQuery query) {
		for(OntologyQueryItem item : query.getRequiredProperties())
		{
			TupleQueryResult result;
			List<OntologyQueryResponse> queryResponse = new ArrayList<>();
			int idx = 0;
			try {
				result = getQueryResult(QueryBuilder.getPropertyDetails(query.getRequiredClass(), item.getPropertyId(), query.getRequiredProperties()));
				
				while (result.hasNext()) { 
					BindingSet bindingSet = result.next();
					OntologyQueryResponse response = new OntologyQueryResponse();
					
					Value label = bindingSet.getValue("label");
					Value description = bindingSet.getValue("description");
					Value propertyValue = bindingSet.getValue("propertyValue");
					if (label != null) response.setLabel(label.stringValue());
					if (description != null) response.setDescription(description.stringValue());
					response.setPropertyValue(propertyValue.stringValue());
					
					queryResponse.add(response);
				}
				item.setQueryResponse(queryResponse);
				
			} catch (QueryEvaluationException | RepositoryException
					| MalformedQueryException e) {
				e.printStackTrace();
				
			}
		}
		return query;
	}

	@Override
	public boolean initialize() {
		try {
			new QueryExecutor(repo).executeUpdate(QueryBuilder.addRequiredTriples());
			return true;
		} catch (UpdateExecutionException | RepositoryException
				| MalformedQueryException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}
