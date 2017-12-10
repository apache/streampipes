package org.streampipes.pe.processors.esper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import org.streampipes.pe.processors.esper.proasense.mhwirth.single.DrillingStartEnrichedController;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.storage.util.Transformer;

public class TestDrillingStartDetectionEnriched {

	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
	{
		DataProcessorInvocation graph = Transformer.fromJsonLd(DataProcessorInvocation.class, FileUtils.readFileToString(new File("src/test/resources/TestDrillingStartEnriched.jsonld"), "UTF-8"));
		new DrillingStartEnrichedController().invokeRuntime(graph);
	}

}
