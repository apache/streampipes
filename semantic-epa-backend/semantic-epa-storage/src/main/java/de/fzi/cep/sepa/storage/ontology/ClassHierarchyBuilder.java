package de.fzi.cep.sepa.storage.ontology;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.NodeType;
import de.fzi.cep.sepa.model.client.ontology.OntologyNode;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;
import de.fzi.cep.sepa.storage.sparql.QueryExecutor;

public class ClassHierarchyBuilder {
	
	private static final String RDFS_SUBCLASS_OF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";

	private Repository repository;
	
	public ClassHierarchyBuilder(Repository repository)
	{
		this.repository = repository;
	}
	
	private List<OntologyNode> getClasses() throws QueryEvaluationException, RepositoryException, MalformedQueryException {		
		TupleQueryResult result = getQueryResult(QueryBuilder.getClasses());
		List<OntologyNode> classNodes = new ArrayList<>();
		while (result.hasNext()) {
			BindingSet bindingSet = result.next();
			Value valueOfX = bindingSet.getValue("result");
			classNodes.add(new OntologyNode(valueOfX.toString(), getLabelName(valueOfX.toString()), NodeType.CLASS));
		}
		return classNodes;
	}
	
	private String getLabelName(String value)
	{
		if (value.contains("#")) return value.substring(value.indexOf("#")+1);
		else if (value.contains("/")) return value.substring(value.lastIndexOf("/")+1);
		else return value;
	}
	
	private TupleQueryResult getQueryResult(String queryString) throws QueryEvaluationException, RepositoryException, MalformedQueryException
	{
		return new QueryExecutor(repository.getConnection(), queryString).execute();
	}
	
	public List<OntologyNode> makeClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		List<OntologyNode> classHierarchy = new ArrayList<>();
		for(OntologyNode node : getClasses())
		{
			List<OntologyNode> children = new ArrayList<>();
			children.addAll(getInstances(node.getId()));
			node.setChildren(children);
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
			result.add(new OntologyNode(valueOfX.toString(), getLabelName(valueOfX.toString()), NodeType.CLASS));
		}
		return result;
	}
	
	private List<OntologyNode> getInstances(String className) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		List<OntologyNode> result = new ArrayList<>();
		TupleQueryResult queryResult = getQueryResult(QueryBuilder.getInstances(className));
		
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();
			Value valueOfX = bindingSet.getValue("s");
			result.add(new OntologyNode(valueOfX.toString(), getLabelName(valueOfX.toString()), NodeType.INSTANCE));
		}
		return result;
	}
}
