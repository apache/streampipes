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

import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import java.util.Arrays;

public class EpRequirements {

  public static EventPropertyList listRequirement() {
    return new EventPropertyList();
  }

  public static EventProperty withMappingPropertyId(String internalId, EventProperty requirement) {
    requirement.setRuntimeName(internalId);
    return requirement;
  }

  public static EventPropertyList nestedListRequirement() {
    EventPropertyList listEp = new EventPropertyList();
    listEp.setEventProperty(new EventPropertyNested());
    return listEp;
  }

  public static EventPropertyList nestedListRequirement(EventPropertyNested nestedRequirement) {
    EventPropertyList listEp = nestedListRequirement();
    listEp.setEventProperty(nestedRequirement);

    return listEp;
  }

  public static EventPropertyList nestedListRequirement(EventProperty... requiredProperties) {
    EventPropertyList listEp = nestedListRequirement();
    EventPropertyNested nested = new EventPropertyNested();
    nested.setEventProperties(Arrays.asList(requiredProperties));
    listEp.setEventProperty(nested);

    return listEp;
  }

  public static EventPropertyList listRequirement(Datatypes datatype) {
    return new EventPropertyList("", datatypeReq(datatype));
  }

  public static EventProperty listRequirement(EventProperty listItemType) {
    return new EventPropertyList(listItemType);
  }

  public static EventPropertyPrimitive datatypeReq(String datatype) {
    EventPropertyPrimitive ep = new EventPropertyPrimitive();
    ep.setRuntimeType(datatype);
    return ep;
  }

  public static EventPropertyPrimitive datatypeReq(Datatypes datatype) {
    return datatypeReq(datatype.toString());
  }

  public static EventPropertyPrimitive booleanReq() {
    return datatypeReq(XSD.BOOLEAN.toString());
  }

  public static EventPropertyPrimitive integerReq() {
    return datatypeReq(XSD.INTEGER.toString());
  }

  public static EventPropertyPrimitive doubleReq() {
    return datatypeReq(XSD.DOUBLE.toString());
  }

  public static EventPropertyPrimitive stringReq() {
    return datatypeReq(XSD.STRING.toString());
  }

  public static EventPropertyPrimitive numberReq() {
    return datatypeReq(SO.NUMBER);
  }

  public static EventPropertyPrimitive anyProperty() {
    return new EventPropertyPrimitive();
  }

  public static EventPropertyPrimitive booleanReq(String semanticType) {
    return appendSemanticType(datatypeReq(XSD.BOOLEAN.toString()), semanticType);
  }

  public static EventPropertyPrimitive integerReq(String semanticType) {
    return appendSemanticType(datatypeReq(XSD.INTEGER.toString()), semanticType);
  }

  public static EventPropertyPrimitive doubleReq(String semanticType) {
    return appendSemanticType(datatypeReq(XSD.DOUBLE.toString()), semanticType);
  }

  public static EventPropertyPrimitive stringReq(String semanticType) {
    return appendSemanticType(datatypeReq(XSD.STRING.toString()), semanticType);
  }

  public static EventPropertyPrimitive numberReq(String semanticType) {
    return appendSemanticType(datatypeReq(SO.NUMBER), semanticType);
  }

  private static <T extends EventProperty> EventProperty semanticTypeReq(String semanticType,
                                                                         Class<T> eventProperty) {
    EventProperty ep = null;
    try {
      ep = eventProperty.newInstance();
      ep.setSemanticType(semanticType);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return ep;
  }

  public static EventPropertyPrimitive semanticTypeReq(String semanticType) {
    return (EventPropertyPrimitive) semanticTypeReq(semanticType, EventPropertyPrimitive.class);
  }

  public static EventPropertyList semanticTypeReqList(String semanticType) {
    return (EventPropertyList) semanticTypeReq(semanticType, EventPropertyList.class);
  }


  private static EventPropertyPrimitive appendSemanticType(EventPropertyPrimitive property, String semanticType) {
    property.setSemanticType(semanticType);
    return property;
  }

  public static EventPropertyPrimitive timestampReq() {
    return semanticTypeReq("http://schema.org/DateTime");
  }
}
