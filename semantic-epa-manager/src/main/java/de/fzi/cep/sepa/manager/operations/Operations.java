package de.fzi.cep.sepa.manager.operations;

import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.pipeline.GraphSubmitter;
import de.fzi.cep.sepa.manager.pipeline.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.pipeline.PipelineValidationHandler;
import de.fzi.cep.sepa.manager.pipeline.TreeBuilder;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

/**
 * class that provides several (partial) pipeline verification methods
 * 
 * @author riemer
 *
 */

public class Operations {

	public static PipelineModificationMessage validatePipeline(Pipeline pipeline, boolean isPartial)
			throws Exception {
		PipelineValidationHandler validator = new PipelineValidationHandler(
				pipeline, isPartial);
		return validator
		.validateConnection()
		.computeMappingProperties()
		.computeMatchingProperties()
		.validateProperties()
		.getPipelineModificationMessage();
	}

	public static void startPipeline(
			de.fzi.cep.sepa.model.client.Pipeline pipeline) {
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false);
		List<SEPAInvocationGraph> graphs = builder.buildGraph();
		new GraphSubmitter(graphs).invokeGraphs();
		
	}
	
}
