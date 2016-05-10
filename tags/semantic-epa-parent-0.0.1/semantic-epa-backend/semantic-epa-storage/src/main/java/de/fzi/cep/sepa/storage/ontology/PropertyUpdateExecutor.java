package de.fzi.cep.sepa.storage.ontology;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.Property;
import de.fzi.cep.sepa.storage.sparql.QueryBuilder;

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
