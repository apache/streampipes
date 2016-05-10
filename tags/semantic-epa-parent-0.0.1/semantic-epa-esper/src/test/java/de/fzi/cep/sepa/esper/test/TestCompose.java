package de.fzi.cep.sepa.esper.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import de.fzi.cep.sepa.esper.compose.ComposeController;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.util.Transformer;

public class TestCompose {

	public TestCompose() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
	{
		SepaInvocation graph = Transformer.fromJsonLd(SepaInvocation.class, FileUtils.readFileToString(new File("src/test/resources/TestCompose.jsonld"), "UTF-8"));
		new ComposeController().invokeRuntime(graph);
	}

}
