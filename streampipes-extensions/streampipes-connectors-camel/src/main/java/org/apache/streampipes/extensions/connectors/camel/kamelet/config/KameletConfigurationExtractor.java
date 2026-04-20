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

package org.apache.streampipes.extensions.connectors.camel.kamelet.config;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletPropertyDefinition;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class KameletConfigurationExtractor {

  public Map<String, Object> buildParams(KameletTemplate template,
                                               RuntimeResolvableGroupStaticProperty parameterGroup) {
    LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();

    for (KameletPropertyDefinition definition : template.properties()) {
      StaticProperty property = findProperty(parameterGroup, definition.staticPropertyInternalName(template.name()));
      if (property == null) {
        if (definition.required()) {
          throw new SpRuntimeException("Missing required Kamelet parameter: " + definition.name());
        }
        continue;
      }

      String value = extractValue(definition, property);
      if (value == null || value.isBlank()) {
        if (definition.required()) {
          throw new SpRuntimeException("Missing required Kamelet parameter: " + definition.name());
        }
        continue;
      }

      parameters.put(definition.name(), value);
    }

    return parameters;
  }

  private StaticProperty findProperty(RuntimeResolvableGroupStaticProperty parameterGroup,
                                      String internalName) {
    return parameterGroup.getStaticProperties()
        .stream()
        .filter(property -> Objects.equals(property.getInternalName(), internalName))
        .findFirst()
        .orElse(null);
  }

  private String extractValue(KameletPropertyDefinition definition,
                              StaticProperty property) {
    return switch (definition.inputType()) {
      case TEXT -> ((FreeTextStaticProperty) property).getValue();
      case SECRET -> ((SecretStaticProperty) property).getValue();
      case ONE_OF -> extractSelectedOption((OneOfStaticProperty) property);
    };
  }

  private String extractSelectedOption(OneOfStaticProperty property) {
    return property.getOptions()
        .stream()
        .filter(Option::isSelected)
        .findFirst()
        .map(option -> option.getInternalName() == null ? option.getName() : option.getInternalName())
        .orElse("");
  }
}
