package de.fzi.cep.sepa.storage.util;

import java.util.List;
import java.util.Optional;

import org.openrdf.repository.RepositoryException;

import de.fzi.cep.sepa.model.client.ontology.Namespace;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class BackgroundKnowledgeUtils {

	public static boolean hasNamespace(String elementId)
	{
		try {
			List<Namespace> namespaces = StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getNamespaces();
			return namespaces.stream().anyMatch(n -> elementId.startsWith(n.getName()));
		} catch (RepositoryException e) {
			return false;
		}
		
	}
	
	public static Optional<Namespace> getNamespace(String elementId)
	{
		try {
			return StorageManager.INSTANCE.getBackgroundKnowledgeStorage().getNamespaces().stream().filter(n -> elementId.startsWith(n.getName())).findFirst();
		} catch (RepositoryException e) {
			return Optional.empty();
		}
	}
}
