package de.fzi.cep.sepa.sdk.helpers;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.eventproperty.QuantitativeValue;
import de.fzi.cep.sepa.model.vocabulary.XSD;
import de.fzi.cep.sepa.sdk.utils.Datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EpProperties {

	public static EventPropertyNested nestedEp(String runtimeName, EventPropertyPrimitive...
					eventProperties) {
		EventPropertyNested nestedProperty = new EventPropertyNested(runtimeName);
		nestedProperty.setEventProperties(new ArrayList<>(Arrays.asList(eventProperties)));
		return nestedProperty;
	}

	public EventPropertyList listNestedEp(String runtimeName, EventPropertyPrimitive...
					nestedProperties) {
		EventPropertyList list = new EventPropertyList();
		list.setRuntimeName(runtimeName);

		EventPropertyNested nested = new EventPropertyNested();
		nested.setEventProperties(Arrays.asList(nestedProperties));
		list.setEventProperties(Arrays.asList(nested));

		return list;
	}

	public static EventPropertyList listIntegerEp(String runtimeName, String domainProperty) {
		return listEp(runtimeName, Datatypes.Integer, domainProperty);
	}

	public static EventPropertyList listLongEp(String runtimeName, String domainProperty) {
		return listEp(runtimeName, Datatypes.Long, domainProperty);
	}

	public static EventPropertyList listDoubleEp(String runtimeName, String domainProperty) {
		return listEp(runtimeName, Datatypes.Double, domainProperty);
	}

	public static EventPropertyList listStringEp(String runtimeName, String domainProperty) {
		return listEp(runtimeName, Datatypes.String, domainProperty);
	}

	public static EventPropertyList listBooleanEp(String runtimeName, String domainProperty) {
		return listEp(runtimeName, Datatypes.Boolean, domainProperty);
	}

	public static EventPropertyList listEp(String runtimeName, Datatypes runtimeType, String
					domainProperty) {
		return new EventPropertyList(runtimeName, ep(runtimeType.toString(),
						runtimeName,
						domainProperty));
	}

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
	
	public static EventPropertyPrimitive integerEp(String runtimeName, String domainProperty, Float minValue, Float maxValue, Float step)
	{
		EventPropertyPrimitive ep =  ep(XSD._integer.toString(), runtimeName, domainProperty);
		ep.setValueSpecification(new QuantitativeValue(minValue, maxValue, step));
		return ep;
	}
	
	public static EventPropertyPrimitive doubleEp(String runtimeName, String domainProperty)
	{
		return ep(XSD._double.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive doubleEp(String runtimeName, String domainProperty, Float minValue, Float maxValue, Float step)
	{
		EventPropertyPrimitive ep =  ep(XSD._double.toString(), runtimeName, domainProperty);
		ep.setValueSpecification(new QuantitativeValue(minValue, maxValue, step));
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
