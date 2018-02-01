package org.streampipes.manager.verification.structure;

import java.util.List;

import org.streampipes.manager.verification.messages.VerificationResult;

public interface Verifier {

	List<VerificationResult> validate();
}
