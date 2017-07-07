package de.fzi.cep.sepa.manager.kpi;

import de.fzi.cep.sepa.kpi.KpiRequest;
import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.client.pipeline.PipelineOperationStatus;
import de.fzi.cep.sepa.storage.controller.StorageManager;

import java.io.IOException;

/**
 * Created by riemer on 03.10.2016.
 */
public class KpiOperations {

    public static Pipeline makeKpiPipeline(KpiRequest kpiRequest, boolean storePipeline) throws IOException {
        Pipeline pipeline;
        if (kpiRequest.getContext() != null) {
            pipeline = new KpiPipelineBuilder(kpiRequest,kpiRequest.getContext()).makePipeline();
        } else {
            pipeline = new KpiPipelineBuilder(kpiRequest).makePipeline();
        }
        if (storePipeline) {
            Operations.storePipeline(pipeline);
            pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(pipeline.getPipelineId());
        }
        return pipeline;
    }

    public static PipelineOperationStatus makeAndStartPipeline(KpiRequest kpiRequest) throws IOException {
        Pipeline pipeline = makeKpiPipeline(kpiRequest, true);
        PipelineOperationStatus status = Operations.startPipeline(pipeline, false, true, false);
        return status;
    }

    public static PipelineOperationStatus stopAndRemovePipeline(String kpiId) {
        PipelineOperationStatus status;
        Pipeline pipeline = StorageManager.INSTANCE.getPipelineStorageAPI().getPipeline(kpiId);
        if (pipeline != null) {
            if (pipeline.isRunning()) {
                status = Operations.stopPipeline(pipeline);
            } else {
                status = new PipelineOperationStatus();
                status.setSuccess(true);
                status.setPipelineId(pipeline.getPipelineId());
            }
            StorageManager.INSTANCE.getPipelineStorageAPI().deletePipeline(kpiId);
            return status;
        }

        throw new IllegalArgumentException("Could not find pipeline");

    }
}
