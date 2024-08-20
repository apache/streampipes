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

package org.apache.streampipes.extensions.api.extractor;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;

import java.util.List;

public interface IParameterExtractor {
  String measurementUnit(String runtimeName, Integer streamIndex);

  String inputTopic(Integer streamIndex);

  Object singleValueParameter(EventPropertyPrimitive targetType, String internalName);

  <V> V singleValueParameter(String internalName, Class<V> targetClass);

  String textParameter(String internalName);

  String secretValue(String internalName);

  boolean slideToggleValue(String internalName);

  String codeblockValue(String internalName);

  String selectedColor(String internalName);

  String selectedFilename(String internalName);

  <V> V selectedSingleValue(String internalName, Class<V> targetClass);

  <V> V selectedSingleValueInternalName(String internalName, Class<V> targetClass);

  List<StaticPropertyGroup> collectionMembersAsGroup(String internalName);

  StaticProperty extractGroupMember(String internalName, StaticPropertyGroup group);

  <V> List<V> singleValueParameterFromCollection(String internalName, Class<V> targetClass);

  <V> List<V> selectedMultiValues(String internalName, Class<V> targetClass);

  <V> List<V> selectedTreeNodesInternalNames(String internalName,
                                             Class<V> targetClass);

  /**
   * @deprecated use {@link #selectedTreeNodesInternalNames(String, Class)} instead
   */
  @Deprecated(since = "0.97.0", forRemoval = true)
  <V> List<V> selectedTreeNodesInternalNames(String internalName,
                                             Class<V> targetClass,
                                             boolean onlyDataNodes);

  <W extends StaticProperty> W getStaticPropertyByName(String internalName, Class<W>
      spType);

  StaticProperty getStaticPropertyByName(String name);

  String mappingPropertyValue(String staticPropertyName);

  List<String> getUnaryMappingsFromCollection(String collectionStaticPropertyName);

  List<String> mappingPropertyValues(String staticPropertyName);

  String propertyDatatype(String runtimeName);

  List<EventProperty> getEventPropertiesBySelector(List<String> selectors) throws
      SpRuntimeException;

  EventProperty getEventPropertyBySelector(String selector) throws SpRuntimeException;

  String getEventPropertyTypeBySelector(String selector) throws SpRuntimeException;

  List<EventProperty> getNoneInputStreamEventPropertySubset(List<String> propertySelectors);

  List<EventProperty> getInputStreamEventPropertySubset(List<String> propertySelectors);

  String selectedAlternativeInternalId(String alternativesInternalId);

  List<String> getEventPropertiesRuntimeNamesByScope(PropertyScope scope);

  List<String> getEventPropertiesSelectorByScope(PropertyScope scope);

  List<EventProperty> getEventPropertiesByScope(PropertyScope scope);
}
