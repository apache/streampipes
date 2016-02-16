package de.fzi.cep.sepa.rest.v2;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.fzi.cep.sepa.model.impl.EcType;
import de.fzi.cep.sepa.model.impl.EpaType;
import de.fzi.cep.sepa.rest.api.AbstractRestInterface;
import de.fzi.cep.sepa.rest.api.v2.Category;
import de.fzi.cep.sepa.storage.controller.StorageManager;

@Path("/v2/categories")
public class CategoryImpl extends AbstractRestInterface implements Category {

	@GET
	@Path("/ep")
	@Produces("application/json")
	@Override
	public String getEps() {
		return toJson(StorageManager.INSTANCE.getStorageAPI().getAllSEPs());
	}

	@GET
	@Path("/epa")
	@Produces("application/json")
	@Override
	public String getEpaCategories() {
		return toJsonWithCustomBuilder(EpaType.values(), false);
	}

	@GET
	@Path("/ec")
	@Produces("application/json")
	@Override
	public String getEcCategories() {
		return toJsonWithCustomBuilder(EcType.values(), false);
	}
}
