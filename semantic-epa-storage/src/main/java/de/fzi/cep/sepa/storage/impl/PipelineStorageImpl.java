package de.fzi.cep.sepa.storage.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.RunningVisualization;
import de.fzi.cep.sepa.model.client.VirtualSensor;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.util.Utils;

import org.apache.shiro.SecurityUtils;
import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by robin on 29.10.14.
 */
public class PipelineStorageImpl extends Storage<Pipeline> implements PipelineStorage {

    Logger LOG = LoggerFactory.getLogger(PipelineStorageImpl.class);

    public PipelineStorageImpl() {
        super(Utils.getCouchDbPipelineClient(), Pipeline.class);
    }

    @Override
    public List<Pipeline> getAllPipelines() {
        List<Pipeline> pipelines = getAll();

        List<Pipeline> result = new ArrayList<>();
        for (Pipeline p : pipelines)
            if (p.getAction() != null) result.add(p);
        return result;
    }

    public List<Pipeline> getAllUserPipelines() {
        CouchDbClient dbClientUser = Utils.getCouchDbUserClient();
        List<Pipeline> pipelines = new ArrayList<>();
        if (SecurityUtils.getSubject().isAuthenticated()) {
            String username = SecurityUtils.getSubject().getPrincipal().toString();
            JsonArray pipelineIds = dbClientUser.view("users/pipelines").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
            for (JsonElement id : pipelineIds) {
                pipelines.add(dbClient.find(Pipeline.class, id.getAsString()));
            }
        }
        return pipelines;
    }

    @Override
    public void storePipeline(Pipeline pipeline) {
        add(pipeline);
    }

    @Override
    public void updatePipeline(Pipeline pipeline) {
        update(pipeline);
    }

    @Override
    public Pipeline getPipeline(String pipelineId) {
        return getWithNullIfEmpty(pipelineId);
    }

    @Override
    public void deletePipeline(String pipelineId) {
        delete(pipelineId);
    }

    @Override
    public void store(Pipeline object) {
        add(object);
    }

    @Override
    public List<RunningVisualization> getRunningVisualizations() {
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
        dbClient.save(visualization);
        dbClient.shutdown();

    }

    @Override
    public void deleteVisualization(String pipelineId) {
        try {
            List<RunningVisualization> currentVisualizations = getRunningVisualizations();
            for (RunningVisualization viz : currentVisualizations)
                if (viz.getPipelineId() != null)
                    if (viz.getPipelineId().equals(pipelineId))
                        dbClient.remove(viz);
        } catch (NoDocumentException e) {
            e.printStackTrace();
        }
        return;

    }

    @Override
    public void storeVirtualSensor(String username, VirtualSensor virtualSensor) {
        dbClient.save(virtualSensor);
        //dbClient.shutdown();
    }

    @Override
    public List<VirtualSensor> getVirtualSensors(String username) {
        List<VirtualSensor> virtualSensors = dbClient.view("_all_docs")
                .includeDocs(true)
                .query(VirtualSensor.class);
        return virtualSensors;
    }
}
