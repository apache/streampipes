package de.fzi.cep.sepa.manager.mapping;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestPipelineExecution {

	public static void main(String[] args) throws InterruptedException
	{
		Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("cd4d999f-615b-4890-9508-dcc98c52b77b");
		
		Operations.startPipeline(pipeline);
		
		Thread.sleep(20000);
		
		Operations.stopPipeline(pipeline);
		
	}
}
