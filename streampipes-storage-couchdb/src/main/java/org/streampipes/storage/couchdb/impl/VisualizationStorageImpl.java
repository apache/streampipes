/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.couchdb.impl;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.streampipes.model.client.RunningVisualization;
import org.streampipes.storage.api.IVisualizationStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class VisualizationStorageImpl extends AbstractDao<RunningVisualization> implements IVisualizationStorage {


    public VisualizationStorageImpl() {
        super(Utils::getCouchDbVisualizationClient, RunningVisualization.class);
    }

    @Override
    public List<RunningVisualization> getRunningVisualizations() {
        CouchDbClient couchDbClient = couchDbClientSupplier.get();
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
        CouchDbClient couchDbClient = couchDbClientSupplier.get();
        couchDbClient.save(visualization);
        couchDbClient.shutdown();

    }

    @Override
    public void deleteVisualization(String pipelineId) {
        try {
            CouchDbClient couchDbClient = couchDbClientSupplier.get();
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
