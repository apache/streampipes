package de.fzi.cep.sepa.manager.pipeline;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestStartPipeline {

	public static void main(String[] args)
	{
		Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getAllPipelines().get(0);
		System.out.println(pipeline.getName());
		
		Operations.startPipeline(pipeline);
	}
}
