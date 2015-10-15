package de.fzi.cep.sepa.storage.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.Namespace;
import de.fzi.cep.sepa.model.client.ontology.NodeType;
import de.fzi.cep.sepa.model.client.ontology.OntologyNode;
import de.fzi.cep.sepa.storage.filter.BackgroundKnowledgeFilter;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;
import de.fzi.cep.sepa.storage.util.BackgroundKnowledgeUtils;

public class ClassHierarchyExecutor extends QueryExecutor {
	
	private static final String RDFS_SUBCLASS_OF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	
	public ClassHierarchyExecutor(Repository repository)
	{
		super(repository);
	}
	
	private List<OntologyNode> getClasses() throws QueryEvaluationException, RepositoryException, MalformedQueryException {		
		TupleQueryResult result = getQueryResult(QueryBuilder.getClasses());
		List<OntologyNode> classNodes = new ArrayList<>();
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfX = bindingSet.getValue("result");
			classNodes.add(makeNode(valueOfX.stringValue(), NodeType.CLASS));
		}
		return BackgroundKnowledgeFilter.classFilter(classNodes);
	}
	
	private String getLabelName(String value)
	{
		if (value.contains("#")) return value.substring(value.indexOf("#")+1);
		else if (value.contains("/")) return value.substring(value.lastIndexOf("/")+1);
		else return value;
	}
	
	private TupleQueryResult getQueryResult(String queryString) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		return executeQuery(queryString);
	}
	
	public List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		List<OntologyNode> classHierarchy = new ArrayList<>();
		for(OntologyNode node : getClasses())
		{
			List<OntologyNode> children = new ArrayList<>();
			children.addAll(getInstances(node.getId()));
			node.setNodes(children);
			classHierarchy.add(node);
		}
		return classHierarchy;
	}
	
	private List<OntologyNode> getSubclasses(String nodeUri) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		List<OntologyNode> result = new ArrayList<>();
		TupleQueryResult queryResult = getQueryResult(QueryBuilder.getSubclasses(nodeUri));
		
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();
			Value valueOfX = bindingSet.getValue("s");
			result.add(makeNode(valueOfX.stringValue(), NodeType.CLASS));
		}
		return result;
	}
	
	private OntologyNode makeNode(String id, NodeType nodeType)
	{
		OntologyNode node;
		Optional<Namespace> ns = BackgroundKnowledgeUtils.getNamespace(id.toString());
		if (ns.isPresent()) node = new OntologyNode(id.toString(), id.toString().replace(ns.get().getName(), ns.get().getPrefix() +":"), ns.get().getPrefix(), ns.get().getName(), NodeType.CLASS);
		else node = new OntologyNode(id.toString(), getLabelName(id.toString()), nodeType);
		
		return node;
	}
	
	private List<OntologyNode> getInstances(String className) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		
		List<OntologyNode> result = new ArrayList<>();
		TupleQueryResult queryResult = getQueryResult(QueryBuilder.getInstances(className));
		
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();
			Value valueOfX = bindingSet.getValue("s");
			result.add(makeNode(valueOfX.toString(), NodeType.INSTANCE));
		}
		return result;
	}
}
