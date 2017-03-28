package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.pipeline.PipelineCategory;

public interface PipelineCategoryStorage {

	List<PipelineCategory> getPipelineCategories();
	
	boolean addPipelineCategory(PipelineCategory pipelineCategory);
	
	boolean deletePipelineCategory(String categoryId);
	
}
