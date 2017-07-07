package de.fzi.cep.sepa.manager.pipeline;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.storage.controller.StorageManager;

/**
 * Created by riemer on 27.09.2016.
 */
public class TestPipelineExecution {

    public static void main(String[] args) {
        Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("850c2850-a27c-4026-939c-171db57c50f0");

        Operations.startPipeline(pipeline);
    }
}
