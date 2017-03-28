package de.fzi.cep.sepa.manager.verification.messages;

import de.fzi.cep.sepa.model.client.messages.Notification;
import de.fzi.cep.sepa.model.client.messages.NotificationType;

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
