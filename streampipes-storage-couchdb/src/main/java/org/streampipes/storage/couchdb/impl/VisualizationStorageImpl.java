package org.streampipes.storage.couchdb.impl;

import org.streampipes.model.client.RunningVisualization;
import org.streampipes.storage.api.VisualizationStorage;
import org.streampipes.storage.couchdb.utils.Utils;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import java.util.ArrayList;
import java.util.List;

public class VisualizationStorageImpl extends Storage<RunningVisualization> implements VisualizationStorage {


    public VisualizationStorageImpl() {
        super(RunningVisualization.class);
    }

    @Override
    protected CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbVisualizationClient();
    }

    @Override
    public List<RunningVisualization> getRunningVisualizations() {
        CouchDbClient dbClient = getCouchDbClient();
        List<RunningVisualization> visualizations = dbClient.view("_all_docs")
                .includeDocs(true)
                .query(RunningVisualization.class);
        List<RunningVisualization> result = new ArrayList<>();
        for (RunningVisualization v : visualizations)
            if (v.getConsumerUrl() != null) result.add(v);
        return result;
    }

    @Override
    public void storeVisualization(RunningVisualization visualization) {
        CouchDbClient dbClient = getCouchDbClient();
        dbClient.save(visualization);
        dbClient.shutdown();

    }

    @Override
    public void deleteVisualization(String pipelineId) {
        try {
            CouchDbClient dbClient = getCouchDbClient();
            List<RunningVisualization> currentVisualizations = getRunningVisualizations();
            for (RunningVisualization viz : currentVisualizations) {
                if (viz.getPipelineId() != null) {
                    if (viz.getPipelineId().equals(pipelineId))
                        dbClient.remove(viz);
                }
            }
            dbClient.shutdown();
        } catch (NoDocumentException e) {
            e.printStackTrace();
        }
        return;

    }
}
