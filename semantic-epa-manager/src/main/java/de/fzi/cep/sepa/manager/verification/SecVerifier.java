package de.fzi.cep.sepa.manager.verification;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;

public class SecVerifier extends ElementVerifier<SecDescription> {

	
	public SecVerifier(String graphData)
			throws SepaParseException {
		super(graphData, de.fzi.cep.sepa.model.impl.graph.SecDescription.class);
	}


	@Override
	protected void store() {
		storageApi.storeSEC(elementDescription);
	}

	@Override
	protected void collectValidators() {
		super.collectValidators();
	}

}
