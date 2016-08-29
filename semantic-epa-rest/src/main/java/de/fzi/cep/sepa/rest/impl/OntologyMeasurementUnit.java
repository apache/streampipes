package de.fzi.cep.sepa.rest.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.fzi.cep.sepa.rest.api.IOntologyMeasurementUnit;
import de.fzi.cep.sepa.units.UnitProvider;

@Path("/v2/units")
public class OntologyMeasurementUnit extends AbstractRestInterface implements IOntologyMeasurementUnit {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	@Path("/instances")
	public String getAllUnits() {
		return toJson(UnitProvider.INSTANCE.getAvailableUnits());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	@Path("/types")
	public String getAllUnitTypes() {
		return toJson(UnitProvider.INSTANCE.getAvailableUnitTypes());
	}

	@GET
	@Path("/instances/{resourceId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getUnit(@PathParam("resourceId") String resourceUri) {
		return toJson(UnitProvider.INSTANCE.getUnit(resourceUri));
	}

}
