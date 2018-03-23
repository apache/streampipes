/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.storage.couchdb.impl;

import org.streampipes.model.client.pipeline.PipelineCategory;
import org.streampipes.storage.api.IPipelineCategoryStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class PipelineCategoryStorageImpl extends AbstractDao<PipelineCategory> implements IPipelineCategoryStorage {

	public PipelineCategoryStorageImpl() {
		super(Utils::getCouchDbPipelineCategoriesClient, PipelineCategory.class);
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
