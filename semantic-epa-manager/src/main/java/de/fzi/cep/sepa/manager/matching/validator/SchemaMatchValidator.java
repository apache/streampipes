package de.fzi.cep.sepa.manager.matching.validator;

import java.util.List;

import de.fzi.cep.sepa.manager.matching.Matcher;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;

public class SchemaMatchValidator implements Validator<List<EventSchema>> {
	
	@Override
	public boolean validate(List<EventSchema> left, List<EventSchema> right) {
		boolean match = true;
		
		for(EventSchema schema : right)
		{
			//no schema restriction defined
			if (schema == null) return true;
			// no property restriction defined
			if (schema.getEventProperties() == null) return true;
			for(EventProperty rightProperty : schema.getEventProperties())
			{
				if (!new Matcher().matches(rightProperty, left.get(0).getEventProperties())) match = false;
			}
		}
		return match;
	}

	@Override
	public boolean validate(List<EventSchema> firstLeft,
			List<EventSchema> secondLeft, List<EventSchema> firstRight,
			List<EventSchema> secondRight) {
		return validate(firstLeft, firstRight) && validate(secondLeft, secondRight);
	}
}
