package de.fzi.cep.sepa.manager.verification;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class SepVerifier extends ElementVerifier<SepDescription>{

	public SepVerifier(String graphData)
			throws SepaParseException {
		super(graphData, de.fzi.cep.sepa.model.impl.graph.SepDescription.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void collectValidators() {
		super.collectValidators();
	}

	@Override
	protected StorageState store(String username, boolean publicElement) {
		StorageState storageState = StorageState.STORED;
		/*
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			StorageManager.INSTANCE.getUserStorageAPI().addSource(username, elementDescription.getElementId());
		}
*/
		if (!storageApi.exists(elementDescription)) storageApi.storeSEP(elementDescription);
		else storageState = StorageState.ALREADY_IN_SESAME;
		if (!(userService.getOwnSourceUris(username).contains(elementDescription.getUri()))) userService.addOwnSource(username, elementDescription.getUri(), publicElement);
		else storageState = StorageState.ALREADY_IN_SESAME_AND_USER_DB;
		return storageState;
	}

	@Override
	protected void update(String username) {
		storageApi.update(elementDescription);
	}
}
