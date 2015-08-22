package de.fzi.cep.sepa.manager.verification;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.storage.controller.StorageManager;

import org.apache.shiro.SecurityUtils;

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
	protected void store(String username, boolean publicElement) {
		/*
		if (SecurityUtils.getSubject().isAuthenticated()) {
			String username = SecurityUtils.getSubject().getPrincipal().toString();
			StorageManager.INSTANCE.getUserStorageAPI().addSource(username, elementDescription.getElementId());
		}
*/
		storageApi.storeSEP(elementDescription);
		userService.addOwnSource(username, elementDescription.getUri(), publicElement);
	}

	@Override
	protected void update(String username) {
		storageApi.update(elementDescription);
	}
}
