package de.fzi.cep.sepa.manager.verification;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;

import org.apache.shiro.SecurityUtils;

public class SepaVerifier extends ElementVerifier<SepaDescription>{

	public SepaVerifier(String graphData)
			throws SepaParseException {
		super(graphData, de.fzi.cep.sepa.model.impl.graph.SepaDescription.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void collectValidators() {
		super.collectValidators();
	}

	@Override
	protected void store(String username, boolean publicElement) {
		storageApi.storeSEPA(elementDescription);
		userService.addOwnSepa(username, elementDescription.getUri(), publicElement);
	}

	@Override
	protected void update(String username) {
		storageApi.update(elementDescription);
	}

}
