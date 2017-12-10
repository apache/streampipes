package org.streampipes.manager.monitoring.runtime;

import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.vocabulary.XSD;

public class RandomDataGenerator {

	Random random;
	
	public RandomDataGenerator()
	{
		random = new Random();
	}
	
	public Object getValue(EventPropertyPrimitive primitive)
	{
		if (primitive.getRuntimeType().equals(getString())) return RandomStringUtils.randomAlphabetic(10);
		else if (primitive.getRuntimeType().equals(getLong())) return random.nextLong();
		else if (primitive.getRuntimeType().equals(getInt())) return random.nextInt();
		else if (primitive.getRuntimeType().equals(getDouble())) return random.nextDouble();
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
