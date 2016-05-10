package de.fzi.cep.sepa.manager.verification.runtime;

import java.util.List;

import de.fzi.cep.sepa.manager.verification.messages.VerificationResult;
import de.fzi.cep.sepa.manager.verification.structure.Verifier;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class SourceSchemaVerifier implements Verifier {

	private SepDescription sep;
	
	public SourceSchemaVerifier(SepDescription sep) {
		this.sep = sep;
	}
	
	@Override
	public List<VerificationResult> validate() {
		// TODO Auto-generated method stub
		return null;
	}

}
