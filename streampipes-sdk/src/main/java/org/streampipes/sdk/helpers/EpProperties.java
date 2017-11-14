package org.streampipes.sdk.helpers;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.*;
import org.streampipes.vocabulary.XSD;
import org.streampipes.sdk.utils.Datatypes;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EpProperties {

	public static EventPropertyNested nestedEp(Label label, String runtimeName, EventProperty...
					eventProperties) {
		EventPropertyNested nestedProperty = getPreparedProperty(label, new EventPropertyNested(runtimeName));
		nestedProperty.setEventProperties(new ArrayList<>(Arrays.asList(eventProperties)));
		return nestedProperty;
	}

	public EventPropertyList listNestedEp(Label label, String runtimeName, EventProperty...
					nestedProperties) {
		EventPropertyList list = getPreparedProperty(label, new EventPropertyList());
		list.setRuntimeName(runtimeName);

		EventPropertyNested nested = new EventPropertyNested();
		nested.setEventProperties(Arrays.asList(nestedProperties));
		list.setEventProperties(Arrays.asList(nested));

		return list;
	}

	public static EventPropertyPrimitive timestampProperty(String runtimeName) {
		// TODO we need a real timestamp property!
		return ep(Labels.from("", "Timestamp", "The current timestamp value"), XSD._long.toString(), runtimeName, "http://schema.org/DateTime");
	}

	public static EventPropertyList listIntegerEp(Label label, String runtimeName, String domainProperty) {
		return listEp(label, runtimeName, Datatypes.Integer, domainProperty);
	}

	public static EventPropertyList listLongEp(Label label, String runtimeName, String domainProperty) {
		return listEp(label, runtimeName, Datatypes.Long, domainProperty);
	}

	public static EventPropertyList listDoubleEp(Label label, String runtimeName, String domainProperty) {
		return listEp(label, runtimeName, Datatypes.Double, domainProperty);
	}

	public static EventPropertyList listStringEp(Label label, String runtimeName, String domainProperty) {
		return listEp(label, runtimeName, Datatypes.String, domainProperty);
	}

	public static EventPropertyList listBooleanEp(Label label, String runtimeName, String domainProperty) {
		return listEp(label, runtimeName, Datatypes.Boolean, domainProperty);
	}

	public static EventPropertyList listEp(Label label, String runtimeName, Datatypes runtimeType, String
					domainProperty) {
		return getPreparedProperty(label, new EventPropertyList(runtimeName, ep(Labels.empty(), runtimeType
										.toString(),
						runtimeName,
						domainProperty)));
	}

	public static EventPropertyPrimitive booleanEp(Label label, String runtimeName, String domainProperty) {
		return ep(label, XSD._boolean.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive stringEp(Label label, String runtimeName, String domainProperty) {
		return ep(label, XSD._string.toString(), runtimeName, domainProperty);
	}

	public static EventPropertyPrimitive stringEp(Label label, String runtimeName, String domainProperty, Enumeration
																								enumeration) {
		EventPropertyPrimitive ep = ep(label, XSD._string.toString(), runtimeName, domainProperty);
		ep.setValueSpecification(enumeration);
		return ep;
	}
	
	public static EventPropertyPrimitive stringEp(Label label, String runtimeName, List<URI> domainProperties) {
		return ep(label, XSD._string.toString(), runtimeName, domainProperties);
	}
	
	public static EventPropertyPrimitive integerEp(Label label, String runtimeName, String domainProperty) {
		return ep(label, XSD._integer.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive integerEp(Label label, String runtimeName, List<URI> domainProperties) {
		return ep(label, XSD._integer.toString(), runtimeName, domainProperties);
	}
	
	public static EventPropertyPrimitive longEp(Label label, String runtimeName, String domainProperty) {
		return ep(label, XSD._long.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive longEp(Label label, String runtimeName, List<URI> domainProperties) {
		return ep(label, XSD._long.toString(), runtimeName, domainProperties);
	}
	
	public static EventPropertyPrimitive integerEp(Label label, String runtimeName, String domainProperty, Float
					minValue, Float maxValue, Float step) {
		return integerEp(label, runtimeName, domainProperty, new QuantitativeValue(minValue, maxValue, step));
	}

	public static EventPropertyPrimitive integerEp(Label label, String runtimeName, String domainProperty,
																								 QuantitativeValue valueSpecification) {
		EventPropertyPrimitive ep =  ep(label, XSD._integer.toString(), runtimeName, domainProperty);
		ep.setValueSpecification(valueSpecification);
		return ep;
	}
	
	public static EventPropertyPrimitive doubleEp(Label label, String runtimeName, String domainProperty) {
		return ep(label, XSD._double.toString(), runtimeName, domainProperty);
	}
	
	public static EventPropertyPrimitive doubleEp(Label label, String runtimeName, String domainProperty, Float minValue,
																								Float maxValue, Float step) {
		EventPropertyPrimitive ep =  ep(label, XSD._double.toString(), runtimeName, domainProperty);
		ep.setValueSpecification(new QuantitativeValue(minValue, maxValue, step));
		return ep;
	}
	
	private static EventPropertyPrimitive ep(Label label, String runtimeType, String runtimeName, String domainProperty)
	{
		return getPreparedProperty(label, new EventPropertyPrimitive(runtimeType, runtimeName, "", Utils.createURI
					(domainProperty)));
	}
	
	private static EventPropertyPrimitive ep(Label label, String runtimeType, String runtimeName, List<URI>
					domainProperties)
	{
		return getPreparedProperty(label, new EventPropertyPrimitive(runtimeType, runtimeName, "", domainProperties));
	}

	private static <T extends EventProperty> T getPreparedProperty(Label label, T eventProperty) {
		eventProperty.setLabel(label.getLabel());
		eventProperty.setDescription(label.getDescription());
		return eventProperty;
	}
}
