package de.fzi.cep.sepa.manager.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonSyntaxException;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

public class TestChartGenerator {

	public static void main(String[] args) throws JsonSyntaxException, IOException, InterruptedException
	{
		System.out.println(StorageManager.INSTANCE.getPipelineStorageAPI().getRunningVisualizations().size());
		
		Pipeline pipeline = Utils.getGson().fromJson(FileUtils.readFileToString(new File("src/test/resources/TestChartGenerator.jsonld"), "UTF-8"), Pipeline.class);
		pipeline.setPipelineId(UUID.randomUUID().toString());
		Operations.startPipeline(pipeline);
		System.out.println(StorageManager.INSTANCE.getPipelineStorageAPI().getRunningVisualizations().size());
		Thread.sleep(10000);
		Operations.stopPipeline(pipeline);
		/*
		GenericTree<NamedSEPAElement> tree = new TreeBuilder(pipeline).generateTree(false);
		InvocationGraphBuilder builder = new InvocationGraphBuilder(tree, false);
		List<InvocableSEPAElement> graphs = builder.buildGraph();
		
		for(InvocableSEPAElement element : graphs)
		{
			if (element instanceof SECInvocationGraph)
			{
				SECInvocationGraph sec = (SECInvocationGraph) element;
				//System.out.println(sec.getUri());
				//System.out.println(sec.getElementId());
				//System.out.println(sec.getName());
			}
			System.out.println("Element ID: " +element.getElementId());
			System.out.println("Belongs To: " +element.getBelongsTo());
			System.out.println("URI: " +element.getUri());
			System.out.println(element.getRdfId().toString());
			//StorageManager.INSTANCE.getStorageAPI().storeInvocableSEPAElement(element);
		}
		*/
		//new GraphSubmitter(graphs).invokeGraphs();
	}
}
