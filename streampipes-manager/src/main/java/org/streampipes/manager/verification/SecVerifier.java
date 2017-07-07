package org.streampipes.manager.verification;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.model.impl.graph.SecDescription;

public class SecVerifier extends ElementVerifier<SecDescription> {

	
	public SecVerifier(String graphData)
			throws SepaParseException {
		super(graphData, SecDescription.class);
	}


	@Override
	protected StorageState store(String username, boolean publicElement) {
		StorageState storageState = StorageState.STORED;
		/*
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			StorageManager.INSTANCE.getUserStorageAPI().addAction(username, elementDescription.getElementId());
		}
*/
		if (!storageApi.exists(elementDescription)) storageApi.storeSEC(elementDescription);
		else storageState = StorageState.ALREADY_IN_SESAME;
		if (!(userService.getOwnActionUris(username).contains(elementDescription.getUri()))) userService.addOwnAction(username, elementDescription.getUri(), publicElement);
		else storageState = StorageState.ALREADY_IN_SESAME_AND_USER_DB;
		return storageState;
	}

	@Override
	protected void collectValidators() {
		super.collectValidators();
	}


	@Override
	protected void update(String username) {
		storageApi.update(elementDescription);
	}

}
