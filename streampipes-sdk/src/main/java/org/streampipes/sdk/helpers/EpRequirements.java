package org.streampipes.sdk.helpers;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.sdk.utils.Datatypes;

public class EpRequirements {

	public static EventPropertyList listRequirement(Datatypes datatype) {
		return new EventPropertyList("", datatypeReq(datatype));
	}

	public static EventPropertyPrimitive datatypeReq(String datatype)
	{
		EventPropertyPrimitive ep = new EventPropertyPrimitive();
		ep.setRuntimeType(datatype);
		return ep;
	}

	public static EventPropertyPrimitive datatypeReq(Datatypes datatype) {
		return datatypeReq(datatype.toString());
	}
	
	public static EventPropertyPrimitive booleanReq()
	{
		return datatypeReq(XSD._boolean.toString());
	}
	
	public static EventPropertyPrimitive integerReq()
	{
		return datatypeReq(XSD._integer.toString());
	}
	
	public static EventPropertyPrimitive doubleReq()
	{
		return datatypeReq(XSD._double.toString());
	}
	
	public static EventPropertyPrimitive stringReq()
	{
		return datatypeReq(XSD._string.toString());
	}
	
	public static EventPropertyPrimitive numberReq() {
		return datatypeReq(SO.Number);
	}

	public static EventPropertyPrimitive anyProperty() {
	    return new EventPropertyPrimitive();
    }


	public static EventPropertyPrimitive booleanReq(String domainProperty)
	{
		return appendDomainProperty(datatypeReq(XSD._boolean.toString()), domainProperty);
	}

	public static EventPropertyPrimitive integerReq(String domainProperty)
	{
		return appendDomainProperty(datatypeReq(XSD._integer.toString()), domainProperty);
	}

	public static EventPropertyPrimitive doubleReq(String domainProperty)
	{
		return appendDomainProperty(datatypeReq(XSD._double.toString()), domainProperty);
	}

	public static EventPropertyPrimitive stringReq(String domainProperty)
	{
		return appendDomainProperty(datatypeReq(XSD._string.toString()), domainProperty);
	}

	public static EventPropertyPrimitive numberReq(String domainProperty) {
		return appendDomainProperty(datatypeReq(SO.Number), domainProperty);
	}
	
	public static EventPropertyPrimitive domainPropertyReq(String domainProperty)
	{
		EventPropertyPrimitive ep = new EventPropertyPrimitive();
		ep.setDomainProperties(Utils.createURI(domainProperty));
		return ep;
	}

	private static EventPropertyPrimitive appendDomainProperty(EventPropertyPrimitive property, String domainProperty) {
		property.setDomainProperties(Utils.createURI(domainProperty));
		return property;
	}

  public static EventPropertyPrimitive timestampReq() {
		return domainPropertyReq("http://schema.org/DateTime");
  }
}
