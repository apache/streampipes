package de.fzi.cep.sepa.model.client.examples;

import de.fzi.cep.sepa.model.client.StreamClient;

public class StreamFactory {

	public static StreamClient generateSEPMock(String name, String description, String sourceId)
	{
		return new StreamClient(name, description, sourceId);
	}
	
	public static StreamClient generateSEPMock(String name, String description, String sourceId, String iconName)
	{
		return new StreamClient(name, description, sourceId, iconName);
	}
}
