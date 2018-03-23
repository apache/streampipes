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

package org.streampipes.storage.rdf4j.util;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.streampipes.model.client.ontology.Namespace;
import org.streampipes.storage.Rdf4JStorageManager;
import org.streampipes.storage.rdf4j.ontology.RangeQueryExecutor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BackgroundKnowledgeUtils {

	public static boolean hasNamespace(String elementId)
	{
		try {
			List<Namespace> namespaces = Rdf4JStorageManager.INSTANCE.getBackgroundKnowledgeStorage().getNamespaces();
			return namespaces.stream().anyMatch(n -> elementId.startsWith(n.getNamespaceId()));
		} catch (RepositoryException e) {
			return false;
		}
		
	}
	
	public static Optional<Namespace> getNamespace(String elementId)
	{
		try {
			return Rdf4JStorageManager.INSTANCE.getBackgroundKnowledgeStorage().getNamespaces().stream().filter(n -> elementId.startsWith(n.getNamespaceId())).findFirst();
		} catch (RepositoryException e) {
			return Optional.empty();
		}
	}
	
	public static Literal parse(String propertyValue, String rdfsType) throws RepositoryException
	{
		Repository repository = Rdf4JStorageManager.INSTANCE.getRepository();
		RepositoryConnection connection = null;
		ValueFactory vf = null;
		
		Literal result;
		try {
			connection = repository.getConnection();
			vf = connection.getValueFactory();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		System.out.println(rdfsType);
		if (rdfsType.startsWith(RangeQueryExecutor.RANGE_TYPE_RDFS_LITERAL.get(0))) result = vf.createLiteral(propertyValue);
		else if (rdfsType.startsWith(RangeQueryExecutor.RANGE_TYPE_RDFS_LITERAL.get(1))) result = vf.createLiteral(Boolean.parseBoolean(propertyValue));
		else if (rdfsType.startsWith(RangeQueryExecutor.RANGE_TYPE_RDFS_LITERAL.get(2))) result = vf.createLiteral(Integer.parseInt(propertyValue));
		else if (rdfsType.startsWith(RangeQueryExecutor.RANGE_TYPE_RDFS_LITERAL.get(3))) result = vf.createLiteral(Double.parseDouble(propertyValue));
		else if (rdfsType.startsWith(RangeQueryExecutor.RANGE_TYPE_RDFS_LITERAL.get(4))) result = vf.createLiteral(Float.parseFloat(propertyValue));
		else 
		{
			connection.close();
			throw new IllegalArgumentException();
		}
		connection.close();
		return result;
	}
	
	public static <T> List<T> filterDuplicates(List<T> nodes) {
		return nodes.parallelStream().distinct().collect(Collectors.toList());	
	}
	
}
