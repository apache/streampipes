package org.streampipes.storage.couchdb.impl;

import java.util.List;

import org.streampipes.model.client.pipeline.PipelineCategory;
import org.streampipes.storage.api.PipelineCategoryStorage;
import org.streampipes.storage.couchdb.utils.Utils;
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
