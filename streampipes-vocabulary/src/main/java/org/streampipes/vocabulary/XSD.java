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

package org.streampipes.vocabulary;

import java.net.URI;

/**
 * The XML Schema vocabulary as URIs
 * 
 * @author mvo
 * 
 */
public class XSD {
	
	/**
	 * The XML Schema Namespace
	 */
	public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";
	
	public static final URI _string = toURI("string");
	
	public static final URI _boolean = toURI("boolean");
	
	public static final URI _float = toURI("float");
	
	public static final URI _double = toURI("double");
	
	public static final URI _decimal = toURI("decimal");

	public static final URI _anyType = toURI("anyType");

	public static final URI _sequence = toURI("sequence");
	/**
	 * As discussed in
	 * http://www.w3.org/2001/sw/BestPractices/XSCH/xsch-sw-20050127
	 * /#section-duration an standardised in
	 * http://www.w3.org/TR/xpath-datamodel/#notation
	 * 
	 * Note: The XML namespace is 'http://www.w3.org/2001/XMLSchema', but RDF
	 * people seems to have agreed on using '#' atht eh end to create a
	 * URI-prefix
	 */
	public static final String XS_URIPREFIX = "http://www.w3.org/2001/XMLSchema#";
	
	/**
	 * According to <a href=
	 * "http://www.w3.org/2001/sw/BestPractices/XSCH/xsch-sw-20050127/#section-duration"
	 * >this</a> SHOULD NOT be used: xsd:duration does not have a well-defined
	 * value space.
	 * 
	 * Instead use _yearMonthDuration or _dayTimeDuration
	 */	
	
	public static final URI _dateTime = toURI("dateTime");
	
	public static final URI _time = toURI("time");
	
	public static final URI _date = toURI("date");
	
	public static final URI _gYearMonth = toURI("gYearMonth");
	
	public static final URI _gYear = toURI("gYear");
	
	public static final URI _gMonthDay = toURI("gMonthDay");
	
	public static final URI _gDay = toURI("gDay");
	
	public static final URI _gMonth = toURI("gMonth");
	
	public static final URI _hexBinary = toURI("hexBinary");
	
	public static final URI _base64Binary = toURI("base64Binary");
	
	public static final URI _anyURI = toURI("anyURI");
	
	public static final URI _QName = toURI("QName");
	
	public static final URI _normalizedString = toURI("normalizedString");
	
	public static final URI _token = toURI("token");
	
	public static final URI _language = toURI("language");
	
	public static final URI _IDREFS = toURI("IDREFS");
	
	public static final URI _ENTITIES = toURI("ENTITIES");
	
	public static final URI _NMTOKEN = toURI("NMTOKEN");
	
	public static final URI _NMTOKENS = toURI("NMTOKENS");
	
	public static final URI _Name = toURI("Name");
	
	public static final URI _NCName = toURI("NCName");
	
	public static final URI _ID = toURI("ID");
	
	public static final URI _IDREF = toURI("IDREF");
	
	public static final URI _ENTITY = toURI("ENTITY");
	
	public static final URI _integer = toURI("integer");
	
	public static final URI _nonPositiveInteger = toURI("nonPositiveInteger");
	
	public static final URI _negativeInteger = toURI("negativeInteger");
	
	public static final URI _long = toURI("long");


	/**
	 * http://www.w3.org/TR/xmlschema-2/datatypes.html#int
	 * 
	 * [Definition:] int is derived from long by setting the value of
	 * maxInclusive to be 2147483647 and minInclusive to be -2147483648. The
	 * base type of int is long.
	 * 
	 */
	public static final URI _int = toURI("int");
	
	public static final URI _short = toURI("short");
	
	public static final URI _byte = toURI("byte");
	
	public static final URI _nonNegativeInteger = toURI("nonNegativeInteger");
	
	public static final URI _unsignedLong = toURI("unsignedLong");
	
	public static final URI _unsignedInt = toURI("unsignedInt");
	
	public static final URI _unsignedShort = toURI("unsignedShort");
	
	public static final URI _unsignedByte = toURI("unsignedByte");
	
	public static final URI _positiveInteger = toURI("positiveInteger");
	
	/**
	 * For convenience: An array of all types in this interface
	 */
	public static final URI[] ALL = new URI[] { _string, _boolean, _float, _double, _decimal,
	        _dateTime, _time, _date, _gYearMonth, _gYear, _gMonthDay, _gDay, _gMonth,
	        _hexBinary, _base64Binary, _anyURI, _QName, _normalizedString, _token, _language,
	        _IDREFS, _ENTITIES, _NMTOKEN, _NMTOKENS, _Name, _NCName, _ID, _IDREF, _ENTITY,
	        _integer, _nonPositiveInteger, _negativeInteger, _long, _int, _short, _byte,
	        _nonNegativeInteger, _unsignedLong, _unsignedInt, _unsignedShort, _unsignedByte,
	        _positiveInteger, _anyType, _sequence

	};

	protected static final URI toURI(String local) {
		return URI.create(XSD_NS + local);
	}
	
}
