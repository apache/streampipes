package de.fzi.cep.sepa.rest.api;


public interface IVirtualSensor {

	String getVirtualSensors(String username);
	
	String addVirtualSensor(String username, String virtualSensorDescription);
}
