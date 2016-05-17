package de.fzi.cep.sepa.rest.api;


public interface VirtualSensor {

	public String getVirtualSensors(String username);
	
	public String addVirtualSensor(String username, String virtualSensorDescription);
}
