package org.streampipes.storage.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import org.streampipes.model.client.ontology.Namespace;
import org.streampipes.storage.controller.StorageManager;
import org.streampipes.storage.ontology.RangeQueryExecutor;

public class BackgroundKnowledgeUtils {

	public static boolean hasNamespace(String elementId)
	{
		try {
			List<Namespace> namespaces = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getNamespaces();
			return namespaces.stream().anyMatch(n -> elementId.startsWith(n.getNamespaceId()));
		} catch (RepositoryException e) {
			return false;
		}
		
	}
	
	public static Optional<Namespace> getNamespace(String elementId)
	{
		try {
			return StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getNamespaces().stream().filter(n -> elementId.startsWith(n.getNamespaceId())).findFirst();
		} catch (RepositoryException e) {
			return Optional.empty();
		}
	}
	
	public static Literal parse(String propertyValue, String rdfsType) throws RepositoryException
	{
		Repository repository = StorageManager.INSTANCE.getRepository();
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
