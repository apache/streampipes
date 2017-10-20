package org.streampipes.container.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.rdf4j.model.Graph;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import org.streampipes.model.transform.JsonLdTransformer;

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
