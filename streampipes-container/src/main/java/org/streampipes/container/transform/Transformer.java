package org.streampipes.container.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.openrdf.model.Graph;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

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
