package org.streampipes.pe.processors.esper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import org.streampipes.pe.processors.esper.proasense.mhwirth.single.DrillingStartEnrichedController;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.storage.util.Transformer;

public class TestDrillingStartDetectionEnriched {

	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
	{
		SepaInvocation graph = Transformer.fromJsonLd(SepaInvocation.class, FileUtils.readFileToString(new File("src/test/resources/TestDrillingStartEnriched.jsonld"), "UTF-8"));
		new DrillingStartEnrichedController().invokeRuntime(graph);
	}

}
