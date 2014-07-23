package de.fzi.cep.sepa.model.client.examples;

import java.util.List;

import de.fzi.cep.sepa.model.client.Domain;
import de.fzi.cep.sepa.model.client.SEPAClient;

public class SEPAFactory {

	public static SEPAClient generateSEPAMock(String name, String description, List<Domain> domains)
	{
		return new SEPAClient(name, description, domains);
	}
}
