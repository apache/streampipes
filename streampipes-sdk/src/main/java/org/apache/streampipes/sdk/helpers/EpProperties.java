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

package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.commons.Utils;
import org.apache.streampipes.model.schema.Enumeration;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.schema.QuantitativeValue;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.SPSensor;
import org.apache.streampipes.vocabulary.XSD;

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

  public static EventPropertyList listNestedEp(Label label, String runtimeName,
                                               List<EventProperty> listItemProperties) {
    EventPropertyList list = getPreparedProperty(label, new EventPropertyList());
    list.setRuntimeName(runtimeName);

    EventPropertyNested nested = new EventPropertyNested();
    nested.setEventProperties(listItemProperties);
    list.setEventProperty(nested);

    return list;
  }

  public static EventPropertyList listNestedEp(Label label, String runtimeName, String
      domainProperty, List<EventProperty> listItemProperties) {
    EventPropertyList list = listNestedEp(label, runtimeName, listItemProperties);
    list.setDomainProperties(Arrays.asList(URI.create(domainProperty)));
    return list;
  }

  /**
   * Creates a new primitive property of type timestamp (with data type long and domain property schema.org/DateTime
   *
   * @param runtimeName The field identifier of the event property at runtime.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive timestampProperty(String runtimeName) {
    // TODO we need a real timestamp property!
    EventPropertyPrimitive ep = ep(Labels.from("", "Timestamp", "The current timestamp value"),
        XSD.LONG.toString(), runtimeName, "http://schema.org/DateTime");
    ep.setPropertyScope(PropertyScope.HEADER_PROPERTY.name());
    return ep;
  }

  /**
   * Creates a new primitive property of type image (with data type string and domain property image
   *
   * @param runtimeName The field identifier of the event property at runtime.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive imageProperty(String runtimeName) {
    EventPropertyPrimitive ep = ep(Labels.from("", "Image", ""),
        XSD.STRING.toString(), runtimeName, SPSensor.IMAGE);
    ep.setPropertyScope(PropertyScope.MEASUREMENT_PROPERTY.name());
    return ep;
  }

  /**
   * Creates a new list-based event property of type integer and with the assigned domain property.
   *
   * @param label          A human-readable label of the property
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyList listIntegerEp(Label label, String runtimeName, String domainProperty) {
    return listEp(label, runtimeName, Datatypes.Integer, domainProperty);
  }

  /**
   * Creates a new list-based event property of type long and with the assigned domain property.
   *
   * @param label          A human-readable label of the property
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyList listLongEp(Label label, String runtimeName, String domainProperty) {
    return listEp(label, runtimeName, Datatypes.Long, domainProperty);
  }

  /**
   * Creates a new list-based event property of type double and with the assigned domain property.
   *
   * @param label          A human-readable label of the property
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyList listDoubleEp(Label label, String runtimeName, String domainProperty) {
    return listEp(label, runtimeName, Datatypes.Double, domainProperty);
  }

  /**
   * Creates a new list-based event property of type string and with the assigned domain property.
   *
   * @param label          A human-readable label of the property
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyList listStringEp(Label label, String runtimeName, String domainProperty) {
    return listEp(label, runtimeName, Datatypes.String, domainProperty);
  }

  /**
   * Creates a new list-based event property of type boolean and with the assigned domain property.
   *
   * @param label          A human-readable label of the property
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
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

  /**
   * Creates a new list-based event property of the parameter type eventProperty
   *
   * @param label         A human-readable label of the property
   * @param runtimeName   The field identifier of the event property at runtime.
   * @param eventProperty The complex type of data in the list
   * @return {@link org.apache.streampipes.model.schema.EventPropertyList}
   */
  public static EventPropertyList listEp(Label label, String runtimeName, EventProperty eventProperty) {
    return getPreparedProperty(label, new EventPropertyList(runtimeName, eventProperty));
  }

  /**
   * Creates a new list-based event property of the parameter type eventProperty
   *
   * @param label         A human-readable label of the property
   * @param runtimeName   The field identifier of the event property at runtime.
   * @param eventProperty The complex type of data in the list
   * @return {@link org.apache.streampipes.model.schema.EventPropertyList}
   */
  public static EventPropertyList listEp(Label label, String runtimeName, EventProperty eventProperty,
                                         String domainProperty) {
    return getPreparedProperty(label, new EventPropertyList(runtimeName, eventProperty, Utils.createURI
        (domainProperty)));
  }

  /**
   * Creates a new primitive property of type boolean and the provided domain property.
   *
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive booleanEp(Label label, String runtimeName, String domainProperty) {
    return ep(label, XSD.BOOLEAN.toString(), runtimeName, domainProperty);
  }

  /**
   * Creates a new primitive property of type number and the provided domain property.
   *
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive numberEp(Label label, String runtimeName, String domainProperty) {
    return ep(label, SO.NUMBER, runtimeName, domainProperty);
  }

  /**
   * Creates a new primitive property of type string and the provided domain property.
   *
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive stringEp(Label label, String runtimeName, String domainProperty) {
    return ep(label, XSD.STRING.toString(), runtimeName, domainProperty);
  }

  /**
   * Creates a new primitive property of type string and the provided domain property.
   *
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @param propertyScope  The scope for the property, whether it is a HEADER, MEASUREMENT, od DIMENSION property
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive stringEp(Label label, String runtimeName, String domainProperty,
                                                PropertyScope propertyScope) {
    EventPropertyPrimitive ep = ep(label, XSD.STRING.toString(), runtimeName, domainProperty);
    ep.setPropertyScope(propertyScope.name());
    return ep;
  }

  /**
   * Creates a new primitive property of type string and the provided domain property. In addition, the value range
   * of the property is restricted to the defined {@link org.apache.streampipes.model.schema.Enumeration}
   *
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @param enumeration    The allowed values of the event property at runtime.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive stringEp(Label label, String runtimeName, String domainProperty, Enumeration
      enumeration) {
    EventPropertyPrimitive ep = ep(label, XSD.STRING.toString(), runtimeName, domainProperty);
    ep.setValueSpecification(enumeration);
    return ep;
  }

  /**
   * Creates a new primitive property of type string and the provided domain property. In addition, the value range
   * of the property is restricted to the defined {@link org.apache.streampipes.model.schema.Enumeration}
   *
   * @param runtimeName      The field identifier of the event property at runtime.
   * @param domainProperties The semantics of the list property as a list of URIs. Use one of the vocabularies
   *                         provided in
   *                         {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive stringEp(Label label, String runtimeName, List<URI> domainProperties) {
    return ep(label, XSD.STRING.toString(), runtimeName, domainProperties);
  }

  /**
   * Creates a new primitive property of type integer and the provided domain property.
   *
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive integerEp(Label label, String runtimeName, String domainProperty) {
    return ep(label, XSD.INTEGER.toString(), runtimeName, domainProperty);
  }

  /**
   * Creates a new primitive property of type integer and the provided domain properties. In addition, the value range
   * of the property is restricted to the defined {@link org.apache.streampipes.model.schema.Enumeration}
   *
   * @param runtimeName      The field identifier of the event property at runtime.
   * @param domainProperties The semantics of the list property as a list of URIs. Use one of the vocabularies
   *                         provided in
   *                         {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive integerEp(Label label, String runtimeName, List<URI> domainProperties) {
    return ep(label, XSD.INTEGER.toString(), runtimeName, domainProperties);
  }

  /**
   * Creates a new primitive property of type integer and the provided domain property.
   *
   * @param label          A human-readable identifier of the property presented to users in the StreamPipes UI.
   *                       If you do not want to have a label besides the runtime name, use
   *                       {@link org.apache.streampipes.sdk.helpers.Labels}
   * @param runtimeName    The field identifier of the event property at runtime.
   * @param domainProperty The semantics of the list property as a String. The string should correspond to a URI
   *                       provided by a vocabulary. Use one of the vocabularies provided in
   *                       {@link org.apache.streampipes.vocabulary} or create your own domain-specific vocabulary.
   * @return {@link org.apache.streampipes.model.schema.EventPropertyPrimitive}
   */
  public static EventPropertyPrimitive longEp(Label label, String runtimeName, String domainProperty) {
    return ep(label, XSD.LONG.toString(), runtimeName, domainProperty);
  }

  public static EventPropertyPrimitive longEp(Label label, String runtimeName, List<URI> domainProperties) {
    return ep(label, XSD.LONG.toString(), runtimeName, domainProperties);
  }

  public static EventPropertyPrimitive integerEp(Label label, String runtimeName, String domainProperty, Float
      minValue, Float maxValue, Float step) {
    return integerEp(label, runtimeName, domainProperty, new QuantitativeValue(minValue, maxValue, step));
  }

  public static EventPropertyPrimitive integerEp(Label label, String runtimeName, String domainProperty,
                                                 QuantitativeValue valueSpecification) {
    EventPropertyPrimitive ep = ep(label, XSD.INTEGER.toString(), runtimeName, domainProperty);
    ep.setValueSpecification(valueSpecification);
    return ep;
  }

  public static EventPropertyPrimitive doubleEp(Label label, String runtimeName, String domainProperty) {
    return ep(label, XSD.DOUBLE.toString(), runtimeName, domainProperty);
  }

  public static EventPropertyPrimitive doubleEp(Label label, String runtimeName, String domainProperty, Float minValue,
                                                Float maxValue, Float step) {
    EventPropertyPrimitive ep = ep(label, XSD.DOUBLE.toString(), runtimeName, domainProperty);
    ep.setValueSpecification(new QuantitativeValue(minValue, maxValue, step));
    return ep;
  }

  public static EventPropertyPrimitive ep(Label label, String runtimeType, String runtimeName, String domainProperty) {
    return getPreparedProperty(label, new EventPropertyPrimitive(runtimeType, runtimeName, "", Utils.createURI
        (domainProperty)));
  }

  private static EventPropertyPrimitive ep(Label label, String runtimeType, String runtimeName, List<URI>
      domainProperties) {
    return getPreparedProperty(label, new EventPropertyPrimitive(runtimeType, runtimeName, "", domainProperties));
  }

  private static <T extends EventProperty> T getPreparedProperty(Label label, T eventProperty) {
    eventProperty.setLabel(label.getLabel());
    eventProperty.setDescription(label.getDescription());
    return eventProperty;
  }
}
