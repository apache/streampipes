package de.fzi.cep.sepa.manager.monitoring.runtime;

import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class RandomDataGenerator {

	Random random;
	
	public RandomDataGenerator()
	{
		random = new Random();
	}
	
	public Object getValue(EventPropertyPrimitive primitive)
	{
		if (primitive.getPropertyType().equals(getString())) return RandomStringUtils.randomAlphabetic(10);
		else if (primitive.getPropertyType().equals(getLong())) return random.nextLong();
		else if (primitive.getPropertyType().equals(getInt())) return random.nextInt();
		else if (primitive.getPropertyType().equals(getDouble())) return random.nextDouble();
		else return random.nextBoolean();
	}
	
	private String getString()
	{
		return XSD._string.toString();
	}
	
	private String getLong()
	{
		return XSD._long.toString();
	}
	
	private String getInt()
	{
		return XSD._integer.toString();
	}
	
	private String getDouble()
	{
		return XSD._double.toString();
	}
}
