package org.streampipes.container.transform;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.empire.core.empire.annotation.InvalidRdfException;
import org.streampipes.serializers.jsonld.JsonLdTransformer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Transformer {

	public static <T> Graph toJsonLd(T element) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException, InvalidRdfException
	{
		return new JsonLdTransformer().toJsonLd(element);
	}
	
	public static <T> T fromJsonLd(Class<T> destination, String json) throws RDFParseException, UnsupportedRDFormatException, IOException, RepositoryException
	{
		return new JsonLdTransformer().fromJsonLd(json, destination);
	}
}
