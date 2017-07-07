package de.fzi.cep.sepa.manager.verification.messages;

import de.fzi.cep.sepa.model.client.messages.NotificationType;

public class VerificationWarning extends VerificationResult {

	public VerificationWarning(NotificationType type) {
		super(type);
	}

}
