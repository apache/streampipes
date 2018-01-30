package org.streampipes.storage.couchdb.impl;

import org.streampipes.model.client.pipeline.PipelineCategory;
import org.streampipes.storage.api.IPipelineCategoryStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class PipelineCategoryStorageImpl extends AbstractDao<PipelineCategory> implements IPipelineCategoryStorage {

	public PipelineCategoryStorageImpl() {
		super(Utils.getCouchDbPipelineCategoriesClient(), PipelineCategory.class);
	}

	@Override
	public List<PipelineCategory> getPipelineCategories() {
		return findAll();
	}

	@Override
	public boolean addPipelineCategory(PipelineCategory pipelineCategory) {
		return persist(pipelineCategory);
	}

	@Override
	public boolean deletePipelineCategory(String categoryId) {
		return delete(categoryId);
	}

}
