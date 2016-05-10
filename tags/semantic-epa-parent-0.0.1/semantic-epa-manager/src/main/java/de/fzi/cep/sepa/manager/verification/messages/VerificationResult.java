package de.fzi.cep.sepa.manager.verification.messages;

import de.fzi.cep.sepa.messages.Notification;
import de.fzi.cep.sepa.messages.NotificationType;

public abstract class VerificationResult {

	NotificationType type;
	
	public VerificationResult(NotificationType type)
	{
		this.type = type;
	}
	
	public Notification getNotification() {
		return new Notification(type.title(), type.description());
	}
	
}
