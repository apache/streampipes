package de.fzi.cep.sepa.manager.verification.structure;

import java.util.List;

import de.fzi.cep.sepa.manager.verification.messages.VerificationResult;

public interface Verifier {

	List<VerificationResult> validate();
}
