package de.fzi.cep.sepa.rest.api;

import javax.ws.rs.core.Response;

public interface IOntologyMeasurementUnit {

	Response getAllUnits();

	Response getUnit(String resourceUri);

	Response getAllUnitTypes();
}
