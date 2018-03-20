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

package org.streampipes.rest.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.streampipes.model.DataSinkType;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.client.Category;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.rest.api.IPipelineElementCategory;
import org.streampipes.storage.management.StorageManager;

@Path("/v2/categories")
public class PipelineElementCategory extends AbstractRestInterface implements IPipelineElementCategory {

	@GET
	@Path("/ep")
	@Produces("application/json")
	@Override
	public Response getEps() {
		return ok(makeCategories(StorageManager.INSTANCE.getStorageAPI().getAllSEPs()));
	}

	@GET
	@Path("/epa")
	@Produces("application/json")
	@Override
	public Response getEpaCategories() {
		return ok(DataProcessorType.values());
	}

	@GET
	@Path("/ec")
	@Produces("application/json")
	@Override
	public Response getEcCategories() {
		return ok(DataSinkType.values());
	}
	
	private List<Category> makeCategories(List<DataSourceDescription> producers) {
		return producers
				.stream()
				.map(p -> new Category(p.getRdfId().toString(), p.getName(), p.getDescription()))
				.collect(Collectors.toList());
	}
}
