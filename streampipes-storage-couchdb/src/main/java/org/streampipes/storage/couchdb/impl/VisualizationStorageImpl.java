package org.streampipes.storage.couchdb.impl;

import org.lightcouch.NoDocumentException;
import org.streampipes.model.client.RunningVisualization;
import org.streampipes.storage.api.IVisualizationStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class VisualizationStorageImpl extends AbstractDao<RunningVisualization> implements IVisualizationStorage {


    public VisualizationStorageImpl() {
        super(Utils.getCouchDbVisualizationClient(), RunningVisualization.class);
    }

    @Override
    public List<RunningVisualization> getRunningVisualizations() {
        List<RunningVisualization> visualizations = couchDbClient.view("_all_docs")
                .includeDocs(true)
                .query(RunningVisualization.class);
        List<RunningVisualization> result = new ArrayList<>();
        for (RunningVisualization v : visualizations)
            if (v.getConsumerUrl() != null) result.add(v);
        return result;
    }

    @Override
    public void storeVisualization(RunningVisualization visualization) {
        couchDbClient.save(visualization);
        couchDbClient.shutdown();

    }

    @Override
    public void deleteVisualization(String pipelineId) {
        try {
            List<RunningVisualization> currentVisualizations = getRunningVisualizations();
            for (RunningVisualization viz : currentVisualizations) {
                if (viz.getPipelineId() != null) {
                    if (viz.getPipelineId().equals(pipelineId))
                        couchDbClient.remove(viz);
                }
            }
            couchDbClient.shutdown();
        } catch (NoDocumentException e) {
            e.printStackTrace();
        }
    }
}
