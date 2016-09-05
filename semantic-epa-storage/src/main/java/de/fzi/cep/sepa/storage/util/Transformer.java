package de.fzi.cep.sepa.storage.util;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import de.fzi.cep.sepa.model.transform.JsonLdTransformer;

public class Transformer {


	public static <T> T fromJsonLd(Class<T> destination, String jsonld) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException {
		return new JsonLdTransformer().fromJsonLd(jsonld, destination);
	}

}
