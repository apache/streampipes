package de.fzi.cep.sepa.manager.verification;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;

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
	protected StorageState store(String username, boolean publicElement) {
		StorageState storageState = StorageState.STORED;
		
		if (!storageApi.exists(elementDescription)) storageApi.storeSEPA(elementDescription);
		else storageState = StorageState.ALREADY_IN_SESAME;
		if (!(userService.getOwnSepaUris(username).contains(elementDescription.getUri()))) userService.addOwnSepa(username, elementDescription.getUri(), publicElement);
		else storageState = StorageState.ALREADY_IN_SESAME_AND_USER_DB;
		return storageState;
	}

	@Override
	protected void update(String username) {
		storageApi.update(elementDescription);
	}

}
