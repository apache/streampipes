package org.streampipes.manager.verification.structure;

import java.util.List;

import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.model.SpDataStream;

public class StreamVerifier extends AbstractVerifier {

	SpDataStream stream;
	
	public StreamVerifier(SpDataStream stream)
	{
		this.stream = stream;
	}
	
	@Override
	public List<VerificationResult> validate() {
		
		
		return validationResults;
	}

}
