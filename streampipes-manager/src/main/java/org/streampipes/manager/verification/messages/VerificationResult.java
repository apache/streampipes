package org.streampipes.manager.verification.messages;

import org.streampipes.model.client.messages.Notification;
import org.streampipes.model.client.messages.NotificationType;

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
