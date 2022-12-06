/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.vocabulary;

import java.net.URI;

@SuppressWarnings("checkstyle:TodoComment")
/**
 * The XML Schema vocabulary as URIs
 *
 * @author mvo
 */
public class XSD {

  /**
   * The XML Schema Namespace
   */
  public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";

  public static final URI STRING = toURI("string");

  public static final URI BOOLEAN = toURI("boolean");

  public static final URI FLOAT = toURI("float");

  public static final URI DOUBLE = toURI("double");

  public static final URI DECIMAL = toURI("decimal");

  public static final URI ANY_TYPE = toURI("anyType");

  public static final URI SEQUENCE = toURI("sequence");
  /**
   * As discussed in
   * http://www.w3.org/2001/sw/BestPractices/XSCH/xsch-sw-20050127
   * /#section-duration an standardised in
   * http://www.w3.org/TR/xpath-datamodel/#notation
   * <p>
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
   * <p>
   * Instead use _yearMonthDuration or _dayTimeDuration
   */

  public static final URI DATE_TIME = toURI("dateTime");

  public static final URI TIME = toURI("time");

  public static final URI DATE = toURI("date");

  public static final URI G_YEAR_MONTH = toURI("gYearMonth");

  public static final URI G_YEAR = toURI("gYear");

  public static final URI G_MONTH_DAY = toURI("gMonthDay");

  public static final URI G_DAY = toURI("gDay");

  public static final URI G_MONTH = toURI("gMonth");

  public static final URI HEX_BINARY = toURI("hexBinary");

  public static final URI BASE_64_BINARY = toURI("base64Binary");

  public static final URI ANY_URI = toURI("anyURI");

  public static final URI Q_NAME = toURI("QName");

  public static final URI NORMALIZED_STRING = toURI("normalizedString");

  public static final URI TOKEN = toURI("token");

  public static final URI LANGUAGE = toURI("language");

  public static final URI IDREFS = toURI("IDREFS");

  public static final URI ENTITIES = toURI("ENTITIES");

  public static final URI NMTOKEN = toURI("NMTOKEN");

  public static final URI NMTOKENS = toURI("NMTOKENS");

  public static final URI NAME = toURI("Name");

  public static final URI NC_NAME = toURI("NCName");

  public static final URI ID = toURI("ID");

  public static final URI IDREF = toURI("IDREF");

  public static final URI ENTITY = toURI("ENTITY");

  public static final URI INTEGER = toURI("integer");

  public static final URI NON_POSITIVE_INTEGER = toURI("nonPositiveInteger");

  public static final URI NEGATIVE_INTEGER = toURI("negativeInteger");

  public static final URI LONG = toURI("long");


  /**
   * http://www.w3.org/TR/xmlschema-2/datatypes.html#int
   * <p>
   * [Definition:] int is derived from long by setting the value of
   * maxInclusive to be 2147483647 and minInclusive to be -2147483648. The
   * base type of int is long.
   */
  public static final URI INT = toURI("int");

  public static final URI SHORT = toURI("short");

  public static final URI BYTE = toURI("byte");

  public static final URI NON_NEGATIVE_INTEGER = toURI("nonNegativeInteger");

  public static final URI UNSIGNED_LONG = toURI("unsignedLong");

  public static final URI UNSIGNED_INT = toURI("unsignedInt");

  public static final URI UNSIGNED_SHORT = toURI("unsignedShort");

  public static final URI UNSIGNED_BYTE = toURI("unsignedByte");

  public static final URI POSITIVE_INTEGER = toURI("positiveInteger");

  /**
   * For convenience: An array of all types in this interface
   */
  public static final URI[] ALL = new URI[]{STRING, BOOLEAN, FLOAT, DOUBLE, DECIMAL,
      DATE_TIME, TIME, DATE, G_YEAR_MONTH, G_YEAR, G_MONTH_DAY, G_DAY, G_MONTH,
      HEX_BINARY, BASE_64_BINARY, ANY_URI, Q_NAME, NORMALIZED_STRING, TOKEN, LANGUAGE,
      IDREFS, ENTITIES, NMTOKEN, NMTOKENS, NAME, NC_NAME, ID, IDREF, ENTITY,
      INTEGER, NON_POSITIVE_INTEGER, NEGATIVE_INTEGER, LONG, INT, SHORT, BYTE,
      NON_NEGATIVE_INTEGER, UNSIGNED_LONG, UNSIGNED_INT, UNSIGNED_SHORT, UNSIGNED_BYTE,
      POSITIVE_INTEGER, ANY_TYPE, SEQUENCE

  };

  protected static URI toURI(String local) {
    return URI.create(XSD_NS + local);
  }

}
