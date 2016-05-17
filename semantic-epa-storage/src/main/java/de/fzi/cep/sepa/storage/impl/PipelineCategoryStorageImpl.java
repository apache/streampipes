package de.fzi.cep.sepa.storage.impl;

import java.util.List;

import org.lightcouch.CouchDbClient;
import org.lightcouch.NoDocumentException;

import de.fzi.cep.sepa.model.client.PipelineCategory;
import de.fzi.cep.sepa.storage.api.PipelineCategoryStorage;
import de.fzi.cep.sepa.storage.util.Utils;

public class PipelineCategoryStorageImpl implements PipelineCategoryStorage {

	@Override
	public List<PipelineCategory> getPipelineCategories() {
		 CouchDbClient dbClient = Utils.getCouchDbPipelineCategoriesClient();
    	 List<PipelineCategory> pipelineCategories = dbClient.view("_all_docs")
    			  .includeDocs(true)
    			  .query(PipelineCategory.class);
    	 
    	 return pipelineCategories;
	}

	@Override
	public boolean addPipelineCategory(PipelineCategory pipelineCategory) {
		CouchDbClient dbClient = Utils.getCouchDbPipelineCategoriesClient();
        dbClient.save(pipelineCategory);
        return true;
	}

	@Override
	public boolean deletePipelineCategory(String categoryId) {
		 CouchDbClient dbClientPipeline = Utils.getCouchDbPipelineCategoriesClient();
	        try {
	            PipelineCategory removePipelineCategory = dbClientPipeline.find(PipelineCategory.class, categoryId);
	            dbClientPipeline.remove(removePipelineCategory);
	            dbClientPipeline.shutdown();
	            return true;
	        } catch (NoDocumentException e) {
	            e.printStackTrace();
	            return false;
	        }
	}
}
