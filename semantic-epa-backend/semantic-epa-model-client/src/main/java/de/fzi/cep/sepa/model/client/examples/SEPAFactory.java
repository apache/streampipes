package de.fzi.cep.sepa.model.client.examples;

import java.util.List;
import de.fzi.cep.sepa.model.client.SEPAClient;

public class SEPAFactory {

	public static SEPAClient generateSEPAMock(String name, String description, List<String> domains)
	{
		return new SEPAClient(name, description, domains);
	}
	
	public static SEPAClient generateSEPAMock(String name, String description, List<String> domains, String iconName)
	{
		return new SEPAClient(name, description, domains, iconName);
	}
}
