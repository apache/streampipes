package de.fzi.cep.sepa.manager.verification.messages;

import de.fzi.cep.sepa.messages.NotificationType;

public class VerificationWarning extends VerificationResult {

	public VerificationWarning(NotificationType type) {
		super(type);
	}

}
