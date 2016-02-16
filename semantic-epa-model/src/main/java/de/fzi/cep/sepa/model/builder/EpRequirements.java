package de.fzi.cep.sepa.model.builder;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.vocabulary.XSD;

public class EpRequirements {

	public static EventPropertyPrimitive datatypeReq(String datatype)
	{
		EventPropertyPrimitive ep = new EventPropertyPrimitive();
		ep.setRuntimeType(datatype);
		return ep;
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
	
	public static EventPropertyPrimitive domainPropertyReq(String domainProperty)
	{
		EventPropertyPrimitive ep = new EventPropertyPrimitive();
		ep.setDomainProperties(Utils.createURI(domainProperty));
		return ep;
	}
}
