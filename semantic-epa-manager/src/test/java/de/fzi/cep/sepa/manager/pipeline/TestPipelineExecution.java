package de.fzi.cep.sepa.manager.pipeline;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;

/**
 * Created by riemer on 27.09.2016.
 */
public class TestPipelineExecution {

    public static void main(String[] args) {
        Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("2720e901-73d8-4d9e-8508-6a89551fe6fe");

        Operations.startPipeline(pipeline);
    }
}
