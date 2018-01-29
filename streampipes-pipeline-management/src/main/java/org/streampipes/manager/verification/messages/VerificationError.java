package org.streampipes.manager.verification.messages;

import org.streampipes.model.client.messages.NotificationType;

public class VerificationError extends VerificationResult{
	
	public VerificationError(NotificationType type)
	{
		super(type);
	}

}
