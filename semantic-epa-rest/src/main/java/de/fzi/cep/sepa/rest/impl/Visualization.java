package de.fzi.cep.sepa.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fzi.cep.sepa.rest.api.IVisualization;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import de.fzi.sepa.model.client.util.Utils;

@Path("/visualizations")
public class Visualization extends AbstractRestInterface implements IVisualization {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunningVisualizations() {
		return ok(StorageManager.INSTANCE.getVisualizationStorageApi().getRunningVisualizations());
	}

}
