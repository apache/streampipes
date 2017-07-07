package de.fzi.cep.sepa.rest.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.rest.api.IPipelineElementCategory;
import de.fzi.cep.sepa.storage.controller.StorageManager;

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
		return ok(EpaType.values());
	}

	@GET
	@Path("/ec")
	@Produces("application/json")
	@Override
	public Response getEcCategories() {
		return ok(EcType.values());
	}
	
	private List<de.fzi.cep.sepa.model.client.Category> makeCategories(List<SepDescription> producers) {
		return producers
				.stream()
				.map(p -> new de.fzi.cep.sepa.model.client.Category(p.getRdfId().toString(), p.getName(), p.getDescription()))
				.collect(Collectors.toList());
	}
}
