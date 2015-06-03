package de.fzi.cep.sepa.manager.matching.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.TransportProtocol;

public class TransportProtocolMatchValidator implements
		Validator<EventGrounding> {

	@Override
	public boolean validate(EventGrounding left, EventGrounding right) {
		boolean protocolMatch = false;
		
		if (left == null || right == null) 
			return true;
		else
		{
			List<TransportProtocol> rightProtocols = right.getTransportProtocols();
			List<TransportProtocol> leftProtocols = left.getTransportProtocols();
			
			protocolMatch = rightProtocols.stream().anyMatch(rightProtocol -> leftProtocols.stream().anyMatch(leftProtocol -> rightProtocol.getClass().getCanonicalName().equals(leftProtocol.getClass().getCanonicalName())));
		}
		return protocolMatch;
	}

	@Override
	public boolean validate(EventGrounding firstLeft,
			EventGrounding secondLeft, EventGrounding firstRight,
			EventGrounding secondRight) {
		return validate(firstLeft, firstRight) && validate(secondLeft, secondRight);
	}

}
