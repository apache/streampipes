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
import org.streampipes.manager.storage.StorageManager;

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
