package org.streampipes.storage.util;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import org.streampipes.model.transform.JsonLdTransformer;

public class Transformer {


	public static <T> T fromJsonLd(Class<T> destination, String jsonld) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException {
		return new JsonLdTransformer().fromJsonLd(jsonld, destination);
	}

}
