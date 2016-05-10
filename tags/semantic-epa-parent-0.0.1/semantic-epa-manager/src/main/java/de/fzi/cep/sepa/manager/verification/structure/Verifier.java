package de.fzi.cep.sepa.manager.verification.structure;

import java.util.List;

import de.fzi.cep.sepa.manager.verification.messages.VerificationResult;

public interface Verifier {

	public List<VerificationResult> validate();
}
