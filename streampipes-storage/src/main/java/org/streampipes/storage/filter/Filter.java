package org.streampipes.storage.filter;

import java.util.List;
import java.util.stream.Collectors;

import org.streampipes.model.NamedSEPAElement;
import org.streampipes.model.client.user.Element;

public class Filter {

	public static <T extends NamedSEPAElement> List<T> byElement(List<T> allElements, List<Element> userElements)
	{
		return allElements.stream().filter(e -> userElements.stream().anyMatch(u -> u.getElementId().equals(e.getUri()))).collect(Collectors.toList());
	}	
	
	public static <T extends NamedSEPAElement> List<T> byUri(List<T> allElements, List<String> userElements)
	{
		return allElements
				.stream()
				.filter(e -> userElements.stream()
						.anyMatch(u -> u.equals(e.getUri()))).collect(Collectors.toList());
	}	
	
	public static <T extends NamedSEPAElement> List<T> addFavorites(List<T> actionClients, List<String> favorites)
	{
		//actionClients.stream().forEach(a -> a.setFavorite(favorites.stream().anyMatch(f -> a.getElementId().equals(f))));
		return actionClients;
	}
	
}
