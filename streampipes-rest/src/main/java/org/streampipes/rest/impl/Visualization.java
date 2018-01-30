package org.streampipes.rest.impl;

import org.streampipes.rest.api.IVisualization;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/visualizations")
public class Visualization extends AbstractRestInterface implements IVisualization {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunningVisualizations() {
		return ok(getVisualizationStorage().getRunningVisualizations());
	}

}
