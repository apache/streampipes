package org.streampipes.manager.verification.runtime;

import java.util.List;

import org.streampipes.manager.verification.messages.VerificationResult;
import org.streampipes.manager.verification.structure.Verifier;
import org.streampipes.model.graph.DataSourceDescription;

public class SourceSchemaVerifier implements Verifier {

	private DataSourceDescription sep;
	
	public SourceSchemaVerifier(DataSourceDescription sep) {
		this.sep = sep;
	}
	
	@Override
	public List<VerificationResult> validate() {
		// TODO Auto-generated method stub
		return null;
	}

}
