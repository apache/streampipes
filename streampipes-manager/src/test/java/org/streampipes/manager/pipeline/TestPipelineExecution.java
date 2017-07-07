package org.streampipes.manager.pipeline;

import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.storage.controller.StorageManager;

/**
 * Created by riemer on 27.09.2016.
 */
public class TestPipelineExecution {

    public static void main(String[] args) {
        Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline("850c2850-a27c-4026-939c-171db57c50f0");

        Operations.startPipeline(pipeline);
    }
}
