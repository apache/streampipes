package de.fzi.cep.sepa.manager.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventQuality;
import de.fzi.cep.sepa.model.impl.EventSchema;

/**
 * 
 * @author riemer
 * factory class to validate event schema, grounding and quality
 *
 */

public class ConnectionValidator {

	public static boolean validateSchema(List<EventSchema> left, List<EventSchema> right)
	{
		return new SchemaMatchValidator().validate(left, right);
	}
	
	public static boolean validateQuality(List<EventQuality> left, List<EventQuality> right)
	{
		return new QualityMatchValidator().validate(left, right);
	}
	
	public static boolean validateGrounding(List<EventGrounding> left, List<EventGrounding> right)
	{
		return new GroundingMatchValidator().validate(left, right);
	}

	public static boolean validateSchema(List<EventSchema> firstLeft, List<EventSchema> secondLeft, List<EventSchema> firstRight, List<EventSchema> secondRight)
	{
		return new SchemaMatchValidator().validate(firstLeft, secondLeft, firstRight, secondRight);
	}
	
	public static boolean validateQuality(List<EventQuality> firstLeft, List<EventQuality> secondLeft, List<EventQuality> right)
	{
		return new QualityMatchValidator().validate(firstLeft, secondLeft, right);
	}
	
	public static boolean validateGrounding(List<EventGrounding> firstLeft, List<EventGrounding> secondLeft, List<EventGrounding> right)
	{
		return new GroundingMatchValidator().validate(firstLeft, secondLeft, right, right);
	}
	
}
