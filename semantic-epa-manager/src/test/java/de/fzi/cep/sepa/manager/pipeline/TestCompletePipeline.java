package de.fzi.cep.sepa.manager.pipeline;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.UnsupportedRDFormatException;

import com.clarkparsia.empire.annotation.InvalidRdfException;
import com.google.gson.JsonSyntaxException;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.execution.http.GraphSubmitter;
import de.fzi.cep.sepa.manager.matching.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.matching.TreeBuilder;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.util.Transformer;
import de.fzi.sepa.model.client.util.Utils;

public class TestCompletePipeline {

	public static void main(String[] args) throws JsonSyntaxException, IOException, RDFHandlerException, IllegalArgumentException, IllegalAccessException, SecurityException, RDFParseException, UnsupportedRDFormatException, RepositoryException, InvocationTargetException, ClassNotFoundException, InvalidRdfException
	{
		Pipeline pipeline = Utils.getGson().fromJson(FileUtils.readFileToString(new File("src/test/resources/TestCompletePipeline.jsonld"), "UTF-8"), Pipeline.class);
		
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false, null);
		List<InvocableSEPAElement> graphs = builder.buildGraph();
		new GraphSubmitter(pipeline.getPipelineId(), pipeline.getName(), graphs).invokeGraphs();
		for(InvocableSEPAElement element : graphs)
		{
			
			if (element instanceof SecInvocation)
			{
				String test = de.fzi.cep.sepa.commons.Utils.asString(Transformer.generateCompleteGraph(new GraphImpl(), element));
				System.out.println(test);
				SecInvocation testGraph = Transformer.fromJsonLd(SecInvocation.class, test);
				System.out.println(testGraph.getDescription());
			}
			
			if (element instanceof SepaInvocation)
			{
				String test = de.fzi.cep.sepa.commons.Utils.asString(Transformer.generateCompleteGraph(new GraphImpl(), element));
				SepaInvocation testGraph = Transformer.fromJsonLd(SepaInvocation.class, test);
			}
		}
		//new GraphSubmitter(graphs).invokeGraphs();
	}
}
