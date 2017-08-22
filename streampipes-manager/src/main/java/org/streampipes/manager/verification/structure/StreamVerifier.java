package org.streampipes.manager.verification.structure;

import java.util.List;

import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.model.impl.EventStream;

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
