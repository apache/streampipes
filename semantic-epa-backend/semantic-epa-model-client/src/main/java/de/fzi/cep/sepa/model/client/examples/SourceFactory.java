package de.fzi.cep.sepa.model.client.examples;

import java.util.List;

import de.fzi.cep.sepa.model.client.Domain;
import de.fzi.cep.sepa.model.client.SourceClient;

public class SourceFactory {

	public static SourceClient generateSourceMock(String name, String description, List<Domain> domain)
	{
		return new SourceClient(name, description, domain);
	}
	
	public static SourceClient generateSourceMock(String name, String description, List<Domain> domain, String iconName)
	{
		return new SourceClient(name, description, domain, iconName);
	}
}
