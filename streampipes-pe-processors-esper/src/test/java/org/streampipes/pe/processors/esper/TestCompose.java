package org.streampipes.pe.processors.esper;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.processors.esper.compose.ComposeController;
import org.streampipes.storage.util.Transformer;

import java.io.File;
import java.io.IOException;

public class TestCompose {

	public TestCompose() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
	{
		DataProcessorInvocation graph = Transformer.fromJsonLd(DataProcessorInvocation.class, FileUtils.readFileToString(new File("src/test/resources/TestCompose.jsonld"), "UTF-8"));
		new ComposeController().invokeRuntime(graph);
	}

}
