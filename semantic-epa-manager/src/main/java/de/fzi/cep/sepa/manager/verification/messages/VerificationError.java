package de.fzi.cep.sepa.manager.verification.messages;

import de.fzi.cep.sepa.model.client.messages.NotificationType;

public class VerificationError extends VerificationResult{
	
	public VerificationError(NotificationType type)
	{
		super(type);
	}

}
