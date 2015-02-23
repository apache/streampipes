package de.fzi.cep.sepa.manager.mapping;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;

public class TestPipelineExecution {

	public static void main(String[] args) throws InterruptedException
	{
		Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("1d598509-f067-489f-bc91-25dbb3198aa2");
		
		Operations.startPipeline(pipeline);
		
		Thread.sleep(20000);
		
		Operations.stopPipeline(pipeline);
		
	}
}
