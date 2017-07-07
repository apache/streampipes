/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
 *
 * License: new BSD
 */
package com.github.jqudt.onto;

import java.io.IOException;
import java.io.InputStream;

import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;

public class OntoReader {

	protected static void read(Model repos, String ontology)
			throws RDFParseException, IOException {
		String filename = "onto/" + ontology;
		InputStream ins = OntoReader.class.getClassLoader()
				.getResourceAsStream(filename);
		repos.addAll(Rio.parse(ins, "",
				Rio.getParserFormatForFileName(ontology, RDFFormat.RDFXML)));
	}
}
