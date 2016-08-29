package de.fzi.cep.sepa.rest.api;

public interface IOntologyMeasurementUnit {

	String getAllUnits();
	
	String getUnit(String resourceUri);

	String getAllUnitTypes();
}
