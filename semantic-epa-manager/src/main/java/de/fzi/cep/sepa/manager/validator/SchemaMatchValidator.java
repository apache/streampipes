package de.fzi.cep.sepa.manager.validator;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;

public class SchemaMatchValidator implements Validator<EventSchema> {

	@Override
	public boolean validate(List<EventSchema> left, List<EventSchema> right) {
		boolean match = false;
		for(EventSchema schema : right)
		{
			for(EventProperty rightProperty : schema.getEventProperties())
			{
				
				for(EventProperty leftProperty : left.get(0).getEventProperties())
				{
					for(URI p : leftProperty.getSubClassOf())
						if (rightProperty.getSubClassOf().contains(p)) match = true;
				}
				if (!match) return false;
				match = false;
			}
		}
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean validate(List<EventSchema> firstLeft,
			List<EventSchema> secondLeft, List<EventSchema> right) {
		return validate(firstLeft, right) && validate(secondLeft, right);
	}
}
