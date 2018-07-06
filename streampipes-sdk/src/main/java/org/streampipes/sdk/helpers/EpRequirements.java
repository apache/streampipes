/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.sdk.helpers;

import org.streampipes.commons.Utils;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyPrimitive;
import org.streampipes.vocabulary.SO;
import org.streampipes.vocabulary.XSD;
import org.streampipes.sdk.utils.Datatypes;

public class EpRequirements {

	public static EventPropertyList listRequirement() {
		return new EventPropertyList();
	}

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

	private static <T extends EventProperty> EventProperty domainPropertyReq(String domainProperty, Class<T> eventProperty)
	{
		EventProperty ep = null;
		try {
			ep = eventProperty.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		ep.setDomainProperties(Utils.createURI(domainProperty));
		return ep;
	}

	public static EventPropertyPrimitive domainPropertyReq(String domainProperty)
	{
	    return (EventPropertyPrimitive) domainPropertyReq(domainProperty, EventPropertyPrimitive.class);
	}

	public static EventPropertyList domainPropertyReqList(String domainProperty)
	{
		return (EventPropertyList) domainPropertyReq(domainProperty, EventPropertyList.class);
	}

	private static EventPropertyPrimitive appendDomainProperty(EventPropertyPrimitive property, String domainProperty) {
		property.setDomainProperties(Utils.createURI(domainProperty));
		return property;
	}

  public static EventPropertyPrimitive timestampReq() {
		return domainPropertyReq("http://schema.org/DateTime");
  }
}
