package de.fzi.cep.sepa.storage.transformer;

import de.fzi.cep.sepa.model.AbstractSEPAElement;

public interface ClientTransformer<SM extends AbstractSEPAElement, CM> {

	public SM toServerModel(CM clientModel);
	
	public CM toClientModel(SM serverModel);
}
