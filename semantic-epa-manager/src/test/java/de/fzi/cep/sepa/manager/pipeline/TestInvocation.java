package de.fzi.cep.sepa.manager.pipeline;

import java.util.List;

import com.google.gson.Gson;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.exceptions.NoValidConnectionException;
import de.fzi.cep.sepa.manager.execution.http.GraphSubmitter;
import de.fzi.cep.sepa.manager.matching.InvocationGraphBuilder;
import de.fzi.cep.sepa.manager.matching.TreeBuilder;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.messages.PipelineOperationStatus;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

public class TestInvocation {

	public static void main(String[] args)
	{
		List<Pipeline> pipelines = StorageManager.INSTANCE.getPipelineStorageAPI().getAllPipelines();
		Pipeline pipeline = null;
		for(Pipeline p : pipelines)
		{
			if (p.getAction() != null)
			{
				pipeline = p;
				break;
			}
		}
		
		System.out.println(Utils.getGson().toJson(pipeline));
		
		String pipelineId = pipeline.getPipelineId();
		System.out.println(pipelineId);
		StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipelineId);
		
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false);
		List<InvocableSEPAElement> graphs = builder.buildGraph();
		PipelineOperationStatus status = new GraphSubmitter(pipelineId, graphs).invokeGraphs();
		System.out.println(new Gson().toJson(status));
		PipelineOperationStatus status2 = new GraphSubmitter(pipelineId, graphs).detachGraphs();
		System.out.println(new Gson().toJson(status2));
		
		/*
		PipelineValidationHandler handler;
		try {
			handler = new PipelineValidationHandler(pipeline, true);
			handler.validateConnection();
			handler.computeMappingProperties();
			PipelineModificationMessage message = handler.getPipelineModificationMessage();
			System.out.println(Utils.getGson().toJson(message));
		} catch (NoValidConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}			
}
