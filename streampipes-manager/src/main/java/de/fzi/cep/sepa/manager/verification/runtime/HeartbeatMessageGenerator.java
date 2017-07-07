package de.fzi.cep.sepa.manager.verification.runtime;

import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

public class HeartbeatMessageGenerator {

	private SepaDescription description;
	
	public HeartbeatMessageGenerator(SepaDescription description)
	{
		this.description = description;
	}
	
	
}
