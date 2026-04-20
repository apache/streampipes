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

import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletPropertyDefinition;
import org.apache.streampipes.extensions.connectors.camel.kamelet.model.KameletTemplate;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.model.staticproperty.OneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.SecretStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Labels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class KameletStaticPropertyGenerator {

  private static final String OPTIONAL_EMPTY_OPTION_LABEL = "Not set";

  public RuntimeResolvableGroupStaticProperty configureParameterGroup(RuntimeResolvableGroupStaticProperty group,
                                                                     KameletTemplate template) {
    group.setShowLabel(false);
    group.setHorizontalRendering(false);
    List<StaticProperty> existingProperties = Objects.nonNull(group.getStaticProperties()) ?  group.getStaticProperties() : new ArrayList<>();
    group.setStaticProperties(template.properties().stream().map(property -> toStaticProperty(template, property, existingProperties)).toList());
    return group;
  }

  private StaticProperty toStaticProperty(KameletTemplate template,
                                          KameletPropertyDefinition propertyDefinition,
                                          List<StaticProperty> existingProperties) {
    String internalName = propertyDefinition.staticPropertyInternalName(template.name());
    var label = Labels.from(
        internalName,
        propertyDefinition.displayLabel(),
        propertyDefinition.description()
    );

    return switch (propertyDefinition.inputType()) {
      case TEXT -> makeTextProperty(label, propertyDefinition, getExisting(internalName, existingProperties, FreeTextStaticProperty.class));
      case SECRET -> makeSecretProperty(label, propertyDefinition, getExisting(internalName, existingProperties, SecretStaticProperty.class));
      case ONE_OF -> makeOneOfProperty(label, propertyDefinition, getExisting(internalName, existingProperties, OneOfStaticProperty.class));
    };
  }

  private StaticProperty makeTextProperty(org.apache.streampipes.sdk.helpers.Label label,
                                          KameletPropertyDefinition definition,
                                          FreeTextStaticProperty existing) {
    FreeTextStaticProperty property = StaticProperties.freeTextProperty(label, definition.datatype());

    property.setOptional(!definition.required());
    if (Objects.nonNull(existing)) {
      property.setValue(existing.getValue());
    } else if (definition.defaultValue() != null) {
      property.setValue(definition.defaultValue());
    }
    return property;
  }

  private StaticProperty makeSecretProperty(org.apache.streampipes.sdk.helpers.Label label,
                                            KameletPropertyDefinition definition,
                                            SecretStaticProperty existing) {
    SecretStaticProperty property = StaticProperties.secretValue(label);
    property.setOptional(!definition.required());
    if (Objects.nonNull(existing)) {
      property.setValue(existing.getValue());
    } else if (definition.defaultValue() != null) {
      property.setValue(definition.defaultValue());
    }
    return property;
  }

  private <T extends StaticProperty> T getExisting(String internalName,
                                                       List<StaticProperty> existingProperties,
                                                       Class<T> targetClass) {
    return existingProperties
        .stream()
        .filter(sp -> sp.getInternalName().equals(internalName))
        .filter(targetClass::isInstance)
        .map(targetClass::cast)
        .findFirst()
        .orElse(null);
  }

  private StaticProperty makeOneOfProperty(org.apache.streampipes.sdk.helpers.Label label,
                                           KameletPropertyDefinition definition,
                                           OneOfStaticProperty existing) {
    List<Option> options = new ArrayList<>();

    if (!definition.required()) {
      Option emptyOption = new Option(OPTIONAL_EMPTY_OPTION_LABEL, "");
      emptyOption.setSelected(definition.defaultValue() == null);
      options.add(emptyOption);
    }

    for (String value : definition.allowedValues()) {
      Option option = new Option(value, value);
      option.setSelected(value.equals(definition.defaultValue()));
      options.add(option);
    }

    Optional<String> existingSelection = getSelectedOptionInternalName(existing);
    if (existingSelection.isPresent()) {
      boolean matched = false;
      for (Option option : options) {
        boolean selected = Objects.equals(option.getInternalName(), existingSelection.get());
        option.setSelected(selected);
        matched |= selected;
      }

      if (!matched) {
        applyDefaultSelection(options);
      }
    } else if (options.stream().noneMatch(Option::isSelected) && !options.isEmpty()) {
      applyDefaultSelection(options);
    }

    OneOfStaticProperty property = StaticProperties.singleValueSelection(label, options);
    property.setOptional(!definition.required());
    return property;
  }

  private Optional<String> getSelectedOptionInternalName(OneOfStaticProperty existing) {
    if (Objects.isNull(existing) || Objects.isNull(existing.getOptions())) {
      return Optional.empty();
    }

    return existing.getOptions()
        .stream()
        .filter(Option::isSelected)
        .map(option -> option.getInternalName() == null ? option.getName() : option.getInternalName())
        .findFirst();
  }

  private void applyDefaultSelection(List<Option> options) {
    if (options.isEmpty()) {
      return;
    }

    options.forEach(option -> option.setSelected(false));
    options.get(0).setSelected(true);
  }
}
