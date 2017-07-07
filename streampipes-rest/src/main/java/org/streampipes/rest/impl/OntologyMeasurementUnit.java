package org.streampipes.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.streampipes.rest.api.IOntologyMeasurementUnit;
import org.streampipes.units.UnitProvider;

@Path("/v2/units")
public class OntologyMeasurementUnit extends AbstractRestInterface implements IOntologyMeasurementUnit {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	@Path("/instances")
	public Response getAllUnits() {
		return ok(UnitProvider
				.INSTANCE
				.getAvailableUnits());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	@Path("/types")
	public Response getAllUnitTypes() {
		return ok(UnitProvider
				.INSTANCE
				.getAvailableUnitTypes());
	}

	@GET
	@Path("/instances/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getUnit(@PathParam("resourceId") String resourceUri) {
		return ok(UnitProvider
				.INSTANCE
				.getUnit(resourceUri));
	}

}
