package org.streampipes.manager.verification.runtime;

import java.util.List;

import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.manager.verification.structure.Verifier;
import org.streampipes.model.impl.graph.SepDescription;

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
