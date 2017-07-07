package org.streampipes.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.streampipes.rest.api.IVisualization;
import org.streampipes.storage.controller.StorageManager;

@Path("/v2/visualizations")
public class Visualization extends AbstractRestInterface implements IVisualization {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunningVisualizations() {
		return ok(StorageManager.INSTANCE.getVisualizationStorageApi().getRunningVisualizations());
	}

}
