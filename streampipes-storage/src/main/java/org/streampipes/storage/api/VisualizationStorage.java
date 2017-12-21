package org.streampipes.storage.api;

import org.streampipes.model.client.RunningVisualization;

import java.util.List;

public interface VisualizationStorage {

    List<RunningVisualization> getRunningVisualizations();

    void storeVisualization(RunningVisualization vizualization);

    void deleteVisualization(String pipelineId);
}
