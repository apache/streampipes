package de.fzi.cep.sepa.manager.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;

public class GroundingMatchValidator implements Validator<EventGrounding> {

	@Override
	public boolean validate(List<EventGrounding> left,
			List<EventGrounding> right) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validate(List<EventGrounding> firstLeft,
			List<EventGrounding> secondLeft, List<EventGrounding> firstRight,
			List<EventGrounding> secondRight) {
		return validate(firstLeft, firstRight) && validate(secondLeft, secondRight);
		
	}

}
