package de.fzi.cep.sepa.esper.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import de.fzi.cep.sepa.esper.drillingstart.single.DrillingStartEnriched;
import de.fzi.cep.sepa.esper.drillingstart.single.DrillingStartEnrichedController;
import de.fzi.cep.sepa.esper.proasense.drillingstart.DrillingStartController;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.storage.util.Transformer;

public class TestDrillingStartDetectionEnriched {

	public static void main(String[] args) throws RDFParseException, UnsupportedRDFormatException, RepositoryException, IOException
	{
		SEPAInvocationGraph graph = Transformer.fromJsonLd(SEPAInvocationGraph.class, FileUtils.readFileToString(new File("src/test/resources/TestDrillingStartEnriched.jsonld"), "UTF-8"));
		new DrillingStartEnrichedController().invokeRuntime(graph);
	}

}
