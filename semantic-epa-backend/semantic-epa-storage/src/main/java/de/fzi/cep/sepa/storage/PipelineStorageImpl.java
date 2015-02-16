package de.fzi.cep.sepa.storage;

import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.RunningVisualization;
import de.fzi.cep.sepa.storage.api.PipelineStorage;
import de.fzi.cep.sepa.storage.util.Utils;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import com.complexible.common.net.VisualAuthenticator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robin on 29.10.14.
 */
public class PipelineStorageImpl implements PipelineStorage {


    @Override
    public List<Pipeline> getAllPipelines() {
    	 CouchDbClient dbClient = Utils.getCouchDBClient();
    	 List<Pipeline> pipelines = dbClient.view("_all_docs")
    			  .includeDocs(true)
    			  .query(Pipeline.class);
    	 
    	 List<Pipeline> result = new ArrayList<>(); 
    	 for(Pipeline p : pipelines) 
    		if (p.getAction() != null) result.add(p);
    	 return result;
    }

    @Override
    public void storePipeline(Pipeline pipeline) {
        CouchDbClient dbClient = Utils.getCouchDBClient();
        dbClient.save(pipeline);
        //dbClient.update(pipeline);

        dbClient.shutdown();
    }

    @Override
    public void updatePipeline(Pipeline pipeline) {
        CouchDbClient dbClient = Utils.getCouchDBClient();
        dbClient.update(pipeline);
        dbClient.shutdown();
    }

    @Override
    public Pipeline getPipeline(String pipelineId) {
        CouchDbClient dbClient = Utils.getCouchDBClient();
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
        CouchDbClient dbClient = Utils.getCouchDBClient();
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
        CouchDbClient dbClient = Utils.getCouchDBClient();
        dbClient.save(object);
        dbClient.shutdown();
    }

	@Override
	public List<RunningVisualization> getRunningVisualizations() {
		 CouchDbClient dbClient = Utils.getCouchDBClient();
    	 List<RunningVisualization> visualizations = dbClient.view("_all_docs")
    			  .includeDocs(true)
    			  .query(RunningVisualization.class);
    	 List<RunningVisualization> result = new ArrayList<>(); 
    	 for(RunningVisualization v : visualizations) 
    		if (v.getConsumerUrl() != null) result.add(v);
    	 return result;
	}

	@Override
	public void storeVisualization(RunningVisualization visualization) {
		CouchDbClient dbClient = Utils.getCouchDBClient();
        dbClient.save(visualization);
        dbClient.shutdown();
		
	}

	@Override
	public void deleteVisualization(String pipelineId) {
					
		CouchDbClient dbClient = Utils.getCouchDBClient();
	        try {
	        	List<RunningVisualization> currentVisualizations = getRunningVisualizations();
	    		for(RunningVisualization viz : currentVisualizations)
	    			if (viz.getPipelineId() != null)
		    			if (viz.getPipelineId().equals(pipelineId))
		    				dbClient.remove(viz);
	            dbClient.shutdown();
	        } catch (NoDocumentException e) {
	            e.printStackTrace();
	        }
	        return;
		
	}
}
