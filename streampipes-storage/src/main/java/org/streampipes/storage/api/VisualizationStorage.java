package org.streampipes.storage.api;

import org.streampipes.model.client.RunningVisualization;

import java.util.List;

/**
 * Created by riemer on 05.09.2016.
 */
public interface VisualizationStorage {

    List<RunningVisualization> getRunningVisualizations();

    void storeVisualization(RunningVisualization vizualization);

    void deleteVisualization(String pipelineId);
}
