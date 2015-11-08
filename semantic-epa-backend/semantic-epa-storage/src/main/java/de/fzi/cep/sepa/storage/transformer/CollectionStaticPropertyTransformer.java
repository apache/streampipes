package de.fzi.cep.sepa.storage.transformer;

import de.fzi.cep.sepa.model.client.input.MultipleValueInput;
import de.fzi.cep.sepa.model.client.input.TextInput;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;

public class CollectionStaticPropertyTransformer implements ClientTransformer<CollectionStaticProperty, MultipleValueInput>{

	@Override
	public CollectionStaticProperty toServerModel(
			MultipleValueInput clientModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultipleValueInput toClientModel(
			CollectionStaticProperty serverModel) {
		// TODO Auto-generated method stub
		return null;
	}

}
