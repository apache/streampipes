package org.streampipes.manager.verification.structure;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.manager.verification.messages.VerificationError;
import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.manager.verification.messages.VerificationWarning;
import org.streampipes.model.client.messages.NotificationType;

public abstract class AbstractVerifier implements Verifier {

	protected List<VerificationResult> validationResults;
	
	public AbstractVerifier()
	{
		this.validationResults = new ArrayList<>();
	}
	
	protected void addWarning(NotificationType notificationType)
	{
		validationResults.add(new VerificationWarning(notificationType));
	}
	
	protected void addError(NotificationType notificationType)
	{
		validationResults.add(new VerificationError(notificationType));
	}
}
