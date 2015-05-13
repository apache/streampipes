package de.fzi.cep.sepa.manager.matching.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.TransportFormat;

public class GroundingMatchValidator implements Validator<EventGrounding> {

	@Override
	public boolean validate(EventGrounding left,
			EventGrounding right) {
		boolean match = false;
		
		if (left == null || right == null) 
			return true;
		else
		{
			List<TransportFormat> rightFormats = right.getTransportFormats();
			List<TransportFormat> leftFormats = left.getTransportFormats();
			rightFormats.forEach(rightFormat -> rightFormat.getRdfType().forEach(rightType -> System.out.println(rightType.toString())));
			match = rightFormats.stream().anyMatch(rightFormat -> leftFormats.stream().anyMatch(leftFormat -> rightFormat.getRdfType().containsAll(leftFormat.getRdfType())));
		}
		return match;
	}

	@Override
	public boolean validate(EventGrounding firstLeft,
			EventGrounding secondLeft, EventGrounding firstRight,
			EventGrounding secondRight) {
		return validate(firstLeft, firstRight) && validate(secondLeft, secondRight);
		
	}

}
