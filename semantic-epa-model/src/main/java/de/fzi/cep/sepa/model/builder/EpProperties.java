package de.fzi.cep.sepa.model.builder;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.staticproperty.PropertyValueSpecification;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class EpProperties {

	public static EventPropertyPrimitive booleanEp(String runtimeName, String domainProperty)
	{
		return ep(XSD._boolean.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive stringEp(String runtimeName, String domainProperty)
	{
		return ep(XSD._string.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive stringEp(String runtimeName, List<URI> domainProperties)
	{
		return ep(XSD._string.toString(), runtimeName, domainProperties);
	}
	
	public static EventPropertyPrimitive integerEp(String runtimeName, String domainProperty)
	{
		return ep(XSD._integer.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive integerEp(String runtimeName, List<URI> domainProperties)
	{
		return ep(XSD._integer.toString(), runtimeName, domainProperties);
	}
	
	public static EventPropertyPrimitive longEp(String runtimeName, String domainProperty)
	{
		return ep(XSD._long.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive longEp(String runtimeName, List<URI> domainProperties)
	{
		return ep(XSD._long.toString(), runtimeName, domainProperties);
	}
	
	public static EventPropertyPrimitive integerEp(String runtimeName, String domainProperty, double minValue, double maxValue, double step)
	{
		EventPropertyPrimitive ep =  ep(XSD._integer.toString(), runtimeName, domainProperty);
		ep.setValueSpecification(new PropertyValueSpecification(minValue, maxValue, step));
		return ep;
	}
	
	public static EventPropertyPrimitive doubleEp(String runtimeName, String domainProperty)
	{
		return ep(XSD._double.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive doubleEp(String runtimeName, String domainProperty, double minValue, double maxValue, double step)
	{
		EventPropertyPrimitive ep =  ep(XSD._double.toString(), runtimeName, domainProperty);
		ep.setValueSpecification(new PropertyValueSpecification(minValue, maxValue, step));
		return ep;
	}
	
	
	
	private static EventPropertyPrimitive ep(String runtimeType, String runtimeName, String domainProperty)
	{
		return new EventPropertyPrimitive(runtimeType, runtimeName, "", Utils.createURI(domainProperty));
	}
	
	private static EventPropertyPrimitive ep(String runtimeType, String runtimeName, List<URI> domainProperties)
	{
		return new EventPropertyPrimitive(runtimeType, runtimeName, "", domainProperties);
	}
}
