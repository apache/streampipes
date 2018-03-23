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

package org.streampipes.sdk;

import org.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.staticproperty.PropertyValueSpecification;
import org.streampipes.model.staticproperty.StaticProperty;
import org.streampipes.model.staticproperty.SupportedProperty;
import org.streampipes.vocabulary.XSD;

import java.net.URI;

public class StaticProperties {

	public static FreeTextStaticProperty stringFreeTextProperty(String internalName, String label, String description) {
		return freeTextProperty(internalName, label, description, XSD._string);
	}
	
	public static FreeTextStaticProperty integerFreeTextProperty(String internalName, String label, String description) {
		return freeTextProperty(internalName, label, description, XSD._integer);
	}
	
	public static FreeTextStaticProperty doubleFreeTextProperty(String internalName, String label, String description) {
		return freeTextProperty(internalName, label, description, XSD._double);
	}
	
	public static FreeTextStaticProperty freeTextProperty(String internalName, String label, String description, URI datatype) {
		FreeTextStaticProperty fsp = new FreeTextStaticProperty(internalName, label, description);
		fsp.setRequiredDatatype(datatype);
		return fsp;
	}

	public static StaticProperty integerFreeTextProperty(String string,
			String string2, String string3,
			PropertyValueSpecification propertyValueSpecification) {
		FreeTextStaticProperty fsp = integerFreeTextProperty(string, string2, string);
		fsp.setValueSpecification(propertyValueSpecification);
		return fsp;
	}

	public static SupportedProperty supportedDomainProperty(String rdfPropertyUri, boolean required) {
		return new SupportedProperty(rdfPropertyUri, required);
	}
}
