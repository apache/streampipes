/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
