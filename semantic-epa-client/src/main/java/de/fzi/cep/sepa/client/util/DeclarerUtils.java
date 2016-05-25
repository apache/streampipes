package de.fzi.cep.sepa.util;

import java.io.IOException;
import java.net.URL;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;

public class DeclarerUtils {

	public static <T> T descriptionFromResources(URL resourceUrl, Class<T> destination) throws SepaParseException
	{
		try {
			return new JsonLdTransformer().fromJsonLd(Resources.toString(resourceUrl, Charsets.UTF_8), destination);
		} catch (RDFParseException | UnsupportedRDFormatException
				| RepositoryException | IOException e) {
			e.printStackTrace();
			throw new SepaParseException();
		}
	}
}
