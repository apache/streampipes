package de.fzi.cep.sepa.manager.validator;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;

public class SchemaMatchValidator implements Validator<EventSchema> {

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
				System.out.println("RIGHT: " +printSubclasses(rightProperty));	
				if (!matches(rightProperty, left.get(0).getEventProperties())) match = false;
			}
		}
		System.out.println("Overall Match: " +match);
		return match;
	}

	
	private boolean matches(EventProperty right, List<EventProperty> left)
	{
		boolean match = false;
		for(EventProperty l : left)
		{
			if (matches(right, l)) match = true;
		}
		return match;
	}
	
	private boolean matches(EventProperty right, EventProperty left)
	{
		boolean match = true;
		System.out.println("Right: " +printSubclasses(right));
		System.out.println("Left: " +printSubclasses(left));
		List<URI> leftUris = left.getSubClassOf();
		for(URI uri : right.getSubClassOf())
		{
			if (!leftUris.contains(uri)) match = false;
		}
		System.out.println("Match: " +match);
		return match;
	}
	
	private String printSubclasses(EventProperty p)
	{
		String result = "(";
		for (URI uri : p.getSubClassOf())
		{
			result = result + uri.toString() +", ";
		}
		result += ")";
		return result;
	}

	@Override
	public boolean validate(List<EventSchema> firstLeft,
			List<EventSchema> secondLeft, List<EventSchema> firstRight,
			List<EventSchema> secondRight) {
		return validate(firstLeft, firstRight) && validate(secondLeft, secondRight);
	}
}
