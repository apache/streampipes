package de.fzi.cep.sepa.manager.verification.structure;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.manager.verification.messages.VerificationError;
import de.fzi.cep.sepa.manager.verification.messages.VerificationResult;
import de.fzi.cep.sepa.manager.verification.messages.VerificationWarning;
import de.fzi.cep.sepa.messages.NotificationType;

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
