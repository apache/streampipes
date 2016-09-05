package de.fzi.cep.sepa.storage.impl;

import java.util.List;

import de.fzi.cep.sepa.model.client.PipelineCategory;
import de.fzi.cep.sepa.storage.api.PipelineCategoryStorage;
import de.fzi.cep.sepa.storage.util.Utils;
import org.lightcouch.CouchDbClient;

public class PipelineCategoryStorageImpl extends Storage<PipelineCategory> implements PipelineCategoryStorage {

	public PipelineCategoryStorageImpl() {
		super(PipelineCategory.class);
	}

	@Override
	public List<PipelineCategory> getPipelineCategories() {
		return getAll();
	}

	@Override
	public boolean addPipelineCategory(PipelineCategory pipelineCategory) {
		add(pipelineCategory);
		return true;
	}

	@Override
	public boolean deletePipelineCategory(String categoryId) {
		return delete(categoryId);
	}

	@Override
	protected CouchDbClient getCouchDbClient() {
		return Utils.getCouchDbPipelineCategoriesClient();
	}
}
