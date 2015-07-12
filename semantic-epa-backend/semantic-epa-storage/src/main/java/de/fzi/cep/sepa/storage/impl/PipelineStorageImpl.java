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
public class PipelineStorageImpl implements PipelineStorage {

	//CouchDbClient dbClient = Utils.getCouchDBClient();
	Logger LOG = LoggerFactory.getLogger(PipelineStorageImpl.class);

    @Override
    public List<Pipeline> getAllPipelines() {
    	 CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
    	 List<Pipeline> pipelines = dbClient.view("_all_docs")
    			  .includeDocs(true)
    			  .query(Pipeline.class);
    	 
    	 List<Pipeline> result = new ArrayList<>(); 
    	 for(Pipeline p : pipelines) 
    		if (p.getAction() != null) result.add(p);
    	 return result;
    }

	public List<Pipeline> getAllUserPipelines() {
		CouchDbClient dbClientUser = Utils.getCouchDbUserClient();
		CouchDbClient dbClientPipeline = Utils.getCouchDbPipelineClient();
		List<Pipeline> pipelines = new ArrayList<>();
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			JsonArray pipelineIds = dbClientUser.view("users/pipelines").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
			for (JsonElement id : pipelineIds) {
				pipelines.add(dbClientPipeline.find(Pipeline.class, id.getAsString()));
			}
		}
		return pipelines;
	}

	/*public static void main(String[] args) {
		CouchDbClient dbClientUser = Utils.getCouchDbUserClient();
		CouchDbClient dbClientPipeline = Utils.getCouchDBClient();
		String username = "user";
		List<Pipeline> pipelines = new ArrayList<>();
		JsonArray pipelineIds = dbClientUser.view("users/pipelines").key(username).query(JsonObject.class).get(0).get("value").getAsJsonArray();
		System.out.println(pipelineIds);
		for (JsonElement id : pipelineIds) {
			pipelines.add(dbClientPipeline.find(Pipeline.class, id.getAsString()));
		}
		System.out.println(pipelines);
	}*/

    @Override
    public void storePipeline(Pipeline pipeline) {
        CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
        dbClient.save(pipeline);
        //dbClient.update(pipeline);

        dbClient.shutdown();
    }

    @Override
    public void updatePipeline(Pipeline pipeline) {
        CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
        dbClient.update(pipeline);
        dbClient.shutdown();
    }

    @Override
    public Pipeline getPipeline(String pipelineId) {
        CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
        try {
            Pipeline pipeline = dbClient.find(Pipeline.class, pipelineId);
            dbClient.shutdown();
            return pipeline;
        } catch (NoDocumentException e) {
			LOG.error("No pipeline wit ID %s found", pipelineId);
            return null;
        }
    }

    @Override
    public void deletePipeline(String pipelineId) {
        CouchDbClient dbClientPipeline = Utils.getCouchDbPipelineClient();
        try {
            Pipeline removePipeline = dbClientPipeline.find(Pipeline.class, pipelineId);
            dbClientPipeline.remove(removePipeline);
            dbClientPipeline.shutdown();
        } catch (NoDocumentException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public <T> void store(T object) {
        CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
        dbClient.save(object);
        dbClient.shutdown();
    }

	@Override
	public List<RunningVisualization> getRunningVisualizations() {
		 CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
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
		CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
        dbClient.save(visualization);
        dbClient.shutdown();
		
	}

	@Override
	public void deleteVisualization(String pipelineId) {
					
		CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
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

	@Override
	public void storeVirtualSensor(VirtualSensor virtualSensor) {
		 CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
		 dbClient.save(virtualSensor);
	     dbClient.shutdown();
	}

	@Override
	public List<VirtualSensor> getVirtualSensors() {
		CouchDbClient dbClient = Utils.getCouchDbPipelineClient();
		List<VirtualSensor> virtualSensors = dbClient.view("_all_docs")
   			  .includeDocs(true)
   			  .query(VirtualSensor.class);
		return virtualSensors;
	}
}
