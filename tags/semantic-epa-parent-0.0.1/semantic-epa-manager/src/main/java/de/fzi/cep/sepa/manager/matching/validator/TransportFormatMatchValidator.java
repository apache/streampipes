package de.fzi.cep.sepa.manager.matching.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.TransportFormat;

public class TransportFormatMatchValidator implements Validator<EventGrounding> {

	@Override
	public boolean validate(EventGrounding left,
			EventGrounding right) {
		boolean formatMatch = false;
	
		if (left == null || right == null) 
			return true;
		else
		{
			List<TransportFormat> rightFormats = right.getTransportFormats();
			List<TransportFormat> leftFormats = left.getTransportFormats();
			
			formatMatch = rightFormats.stream().anyMatch(rightFormat -> leftFormats.stream().anyMatch(leftFormat -> rightFormat.getRdfType().containsAll(leftFormat.getRdfType())));
		}
		return formatMatch;
	}

	@Override
	public boolean validate(EventGrounding firstLeft,
			EventGrounding secondLeft, EventGrounding firstRight,
			EventGrounding secondRight) {
		return validate(firstLeft, firstRight) && validate(secondLeft, secondRight);
		
	}

}
