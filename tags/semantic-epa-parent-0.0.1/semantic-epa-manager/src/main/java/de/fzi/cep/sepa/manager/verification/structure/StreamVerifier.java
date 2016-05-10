package de.fzi.cep.sepa.manager.verification.structure;

import java.util.List;

import de.fzi.cep.sepa.manager.verification.messages.VerificationResult;
import de.fzi.cep.sepa.model.impl.EventStream;

public class StreamVerifier extends AbstractVerifier {

	EventStream stream;
	
	public StreamVerifier(EventStream stream)
	{
		this.stream = stream;
	}
	
	@Override
	public List<VerificationResult> validate() {
		
		
		return validationResults;
	}

}
