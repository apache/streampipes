package org.streampipes.manager.verification.messages;

import org.streampipes.model.client.messages.NotificationType;

public class VerificationWarning extends VerificationResult {

	public VerificationWarning(NotificationType type) {
		super(type);
	}

}
