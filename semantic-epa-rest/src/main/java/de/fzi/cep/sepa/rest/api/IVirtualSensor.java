package de.fzi.cep.sepa.rest.api;


import javax.ws.rs.core.Response;

public interface IVirtualSensor {

	Response getVirtualSensors(String username);
	
	Response addVirtualSensor(String username, String virtualSensorDescription);
}
