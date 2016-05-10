package de.fzi.cep.sepa.storage.api;

import java.util.List;

import de.fzi.cep.sepa.model.client.PipelineCategory;

public interface PipelineCategoryStorage {

	public List<PipelineCategory> getPipelineCategories();
	
	public boolean addPipelineCategory(PipelineCategory pipelineCategory);
	
	public boolean deletePipelineCategory(String categoryId);
	
}
