package de.fzi.cep.sepa.manager.matching.validator;

import java.util.List;

import de.fzi.cep.sepa.manager.matching.QualityMatcher;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQualityRequirement;

public class QualityMatchValidator implements Validator<List<EventStream>>{

	@Override
	public boolean validate(List<EventStream> left, List<EventStream> right) {
		boolean match = true;
		
		for (EventStream stream : right) {
			List<EventStreamQualityRequirement> streamQualities = stream.getRequiresEventStreamQualities();

			if (streamQualities == null || streamQualities.size() == 0) return match;
			
			
//			new QualityMatcher().matches(left.get(0), stream);
			// if query size = 0 false otherwise further checking
			
		}

		return false;
	}

	@Override
	public boolean validate(List<EventStream> firstLeft, List<EventStream> secondLeft, List<EventStream> firstRight,
			List<EventStream> secondRight) {
		// TODO Auto-generated method stub
		return false;
	}

}
