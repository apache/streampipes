package org.streampipes.storage.rdf4j.ontology;


import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.UpdateExecutionException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.streampipes.model.client.ontology.Property;
import org.streampipes.storage.rdf4j.sparql.QueryBuilder;

public class PropertyUpdateExecutor extends UpdateExecutor{

	private Property property;
	
	public PropertyUpdateExecutor(Repository repository, Property property)
	{
		super(repository);
		this.property = property;
	}
	
	public void deleteExistingTriples() throws UpdateExecutionException, RepositoryException, MalformedQueryException
	{
		executeUpdate(QueryBuilder.deletePropertyDetails(property.getElementHeader().getId()));
	}
	
	public void addNewTriples() throws UpdateExecutionException, RepositoryException, MalformedQueryException 
	{	
		executeUpdate(QueryBuilder.addPropertyDetails(property));
	}
}
