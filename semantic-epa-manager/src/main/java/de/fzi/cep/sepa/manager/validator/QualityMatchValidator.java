package de.fzi.cep.sepa.manager.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventQuality;

public class QualityMatchValidator implements Validator<EventQuality> {

	@Override
	public boolean validate(List<EventQuality> left, List<EventQuality> right) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validate(List<EventQuality> firstLeft,
			List<EventQuality> secondLeft, List<EventQuality> right) {
		return validate(firstLeft, right) && validate(secondLeft, right);
	}

}
