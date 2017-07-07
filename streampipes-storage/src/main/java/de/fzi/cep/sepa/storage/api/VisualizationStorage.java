package de.fzi.cep.sepa.storage.api;

import de.fzi.cep.sepa.model.client.RunningVisualization;

import java.util.List;

/**
 * Created by riemer on 05.09.2016.
 */
public interface VisualizationStorage {

    List<RunningVisualization> getRunningVisualizations();

    void storeVisualization(RunningVisualization vizualization);

    void deleteVisualization(String pipelineId);
}
