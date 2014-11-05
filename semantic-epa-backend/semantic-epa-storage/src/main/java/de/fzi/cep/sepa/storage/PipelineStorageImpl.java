package de.fzi.cep.sepa.storage;

import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.cep.sepa.storage.impl.StorageRequestsImpl;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbClientBase;
import org.lightcouch.NoDocumentException;

import java.util.List;

/**
 * Created by robin on 29.10.14.
 */
public class PipelineStorageImpl implements PipelineStorage {


    @Override
    public List<Pipeline> getAllPipelines() {
        return null;
    }

    @Override
    public void storePipeline(Pipeline pipeline) {
        CouchDbClient dbClient = new CouchDbClient();
        dbClient.save(pipeline);
        dbClient.update(pipeline);

        dbClient.shutdown();
    }

    @Override
    public void updatePipeline(Pipeline pipeline) {
        //CouchDbClient dbClient = new CouchDbClient();
    }

    @Override
    public Pipeline getPipeline(String pipelineId) {
        CouchDbClient dbClient = new CouchDbClient();
        try {
            Pipeline pipeline = dbClient.find(Pipeline.class, pipelineId);
            dbClient.shutdown();
            return pipeline;
        } catch (NoDocumentException e) {
            return null;
        }
    }

    @Override
    public void deletePipeline(String pipelineId) {
        CouchDbClient dbClient = new CouchDbClient();
        try {
            Pipeline removePipeline = dbClient.find(Pipeline.class, pipelineId);
            dbClient.remove(removePipeline);
            dbClient.shutdown();
        } catch (NoDocumentException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public <T> void store(T object) {
        CouchDbClient dbClient = new CouchDbClient();
        dbClient.save(object);
    }
}
