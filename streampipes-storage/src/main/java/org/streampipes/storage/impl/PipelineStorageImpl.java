package org.streampipes.storage.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.streampipes.model.client.pipeline.Pipeline;
import org.streampipes.model.client.VirtualSensor;
import org.streampipes.storage.api.PipelineStorage;
import org.streampipes.storage.util.Utils;

import org.apache.shiro.SecurityUtils;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineStorageImpl extends Storage<Pipeline> implements PipelineStorage {

    Logger LOG = LoggerFactory.getLogger(PipelineStorageImpl.class);

    private static final String SYSTEM_USER = "system";

    public PipelineStorageImpl() {
        super(Pipeline.class);
    }

    @Override
    public List<Pipeline> getAllPipelines() {
        List<Pipeline> pipelines = getAll();

        List<Pipeline> result = new ArrayList<>();
        for (Pipeline p : pipelines)
            if (p.getActions() != null) result.add(p);
        return result;
    }

    @Override
    public List<Pipeline> getSystemPipelines() {
        List<Pipeline> pipelines = getAllPipelines();
        return pipelines
                .stream()
                .filter(p -> p.getCreatedByUser().equals(SYSTEM_USER))
                .collect(Collectors.toList());
    }

    public List<Pipeline> getAllUserPipelines() {
        CouchDbClient dbClientUser = Utils.getCouchDbUserClient();
        List<Pipeline> pipelines = new ArrayList<>();
        if (SecurityUtils.getSubject().isAuthenticated()) {
            String username = SecurityUtils.getSubject().getPrincipal().toString();
            JsonArray pipelineIds = dbClientUser.view("users/pipelines").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
            for (JsonElement id : pipelineIds) {
                pipelines.add(getPipeline(id.getAsString()));
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
    public void storeVirtualSensor(String username, VirtualSensor virtualSensor) {
        CouchDbClient dbClient = getCouchDbClient();
        dbClient.save(virtualSensor);
        dbClient.shutdown();
    }

    @Override
    public List<VirtualSensor> getVirtualSensors(String username) {
        CouchDbClient dbClient = getCouchDbClient();
        List<VirtualSensor> virtualSensors = dbClient.view("_all_docs")
                .includeDocs(true)
                .query(VirtualSensor.class);
        dbClient.shutdown();
        return virtualSensors;
    }

    @Override
    protected CouchDbClient getCouchDbClient() {
        return Utils.getCouchDbPipelineClient();
    }
}
