package de.fzi.cep.sepa.sdk;

import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.PropertyValueSpecification;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty;
import de.fzi.cep.sepa.model.vocabulary.XSD;

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
