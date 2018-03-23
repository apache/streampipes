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

package org.streampipes.storage.api;

import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.streampipes.model.client.ontology.Concept;
import org.streampipes.model.client.ontology.Instance;
import org.streampipes.model.client.ontology.Namespace;
import org.streampipes.model.client.ontology.OntologyNode;
import org.streampipes.model.client.ontology.OntologyQuery;
import org.streampipes.model.client.ontology.Property;
import org.streampipes.model.client.ontology.Resource;

import java.util.List;


public interface IBackgroundKnowledgeStorage {

	List<OntologyNode> getClassHierarchy() throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	List<OntologyNode> getPropertyHierarchy() throws RepositoryException, MalformedQueryException, QueryEvaluationException;
	
	
	Property getProperty(String typeId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	Concept getConcept(String conceptId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	Instance getInstance(String instanceId) throws QueryEvaluationException, RepositoryException, MalformedQueryException;
	
	
	boolean addProperty(Resource resource);
	
	boolean addConcept(Resource resource);
	
	boolean addIndividual(Resource resource);
	
	
	boolean updateProperty(Property property);
	
	boolean updateConcept(Concept concept);
	
	boolean updateInstance(Instance instance);
	
	List<Namespace> getNamespaces() throws RepositoryException;
	
	boolean addNamespace(Namespace namespace);
	
	boolean deleteNamespace(String prefix);
	
	boolean deleteResource(String resourceId);
	
	OntologyQuery getOntologyResult(OntologyQuery query);
	
	boolean initialize();
	
	
}
