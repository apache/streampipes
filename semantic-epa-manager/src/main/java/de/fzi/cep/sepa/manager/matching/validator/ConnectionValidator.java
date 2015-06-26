package de.fzi.cep.sepa.manager.matching.validator;

import java.util.List;

import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.quality.EventStreamQuality;

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
	

	public static boolean validateTransportFormat(EventGrounding left, EventGrounding right)
	{
		return new TransportFormatMatchValidator().validate(left, right);
	}
	
	public static boolean validateTransportProtocol(EventGrounding left, EventGrounding right)
	{
		return new TransportProtocolMatchValidator().validate(left, right);
	}

	public static boolean validateSchema(List<EventSchema> firstLeft, List<EventSchema> secondLeft, List<EventSchema> firstRight, List<EventSchema> secondRight)
	{
		return new SchemaMatchValidator().validate(firstLeft, secondLeft, firstRight, secondRight);
	}
	

	public static boolean validateGrounding(EventGrounding firstLeft, EventGrounding secondLeft, EventGrounding right)
	{
		return new TransportFormatMatchValidator().validate(firstLeft, secondLeft, right, right);
	}
	
	public static boolean validateTransportFormat(EventGrounding firstLeft, EventGrounding secondLeft, EventGrounding right)
	{
		return new TransportFormatMatchValidator().validate(firstLeft, secondLeft, right, right);
	}
	
	public static boolean validateTransportProtocol(EventGrounding firstLeft, EventGrounding secondLeft, EventGrounding right)
	{
		return new TransportProtocolMatchValidator().validate(firstLeft, secondLeft, right, right);
	}

	
	
}
